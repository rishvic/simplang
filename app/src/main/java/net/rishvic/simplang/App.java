package net.rishvic.simplang;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
import net.rishvic.simplang.analysis.ParseTable;
import net.rishvic.simplang.analysis.Simplifications;
import net.rishvic.simplang.antlr.SimpLangLexer;
import net.rishvic.simplang.antlr.SimpLangParser;
import net.rishvic.simplang.compiler.Driver;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.text.StringEscapeUtils;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "simplang",
    mixinStandardHelpOptions = true,
    version = "SimpLang v1.0.0-SNAPSHOT",
    description = "Parses & analyzes SimpLang language specifications.",
    subcommands = {Simplify.class, FirstFollow.class, Parser.class})
public class App implements Callable<Integer> {
  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() {
    return 0;
  }
}

@Command(
    name = "ll-simplify",
    mixinStandardHelpOptions = true,
    description = "Simplifies grammar, removing left recursion & left factoring.")
class Simplify implements Callable<Integer> {

  @Option(names = "-p", description = "Pretty print grammar")
  boolean pretty;

  @Parameters(index = "0")
  File file;

  @Override
  public Integer call() throws IOException {
    InputStream inputStream = new FileInputStream(file);
    CharStream charStream = CharStreams.fromStream(inputStream);

    SimpLangLexer lexer = new SimpLangLexer(charStream);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    SimpLangParser parser = new SimpLangParser(tokenStream);
    ParseTree tree = parser.source();

    Driver compiler = new Driver();
    compiler.visit(tree);

    Map<String, List<List<String>>> simplifiedGrammar =
        Simplifications.leftFactor(
            Simplifications.removeLeftRecursion(compiler.getProductionRules()));

    System.out.printf(
        "%s",
        pretty
            ? Simplifications.toPrettyString(simplifiedGrammar, compiler.getTerminalRules())
            : Simplifications.toGrammarString(simplifiedGrammar, compiler.getTerminalRules()));

    return 0;
  }
}

@Command(
    name = "ll-first-follow",
    mixinStandardHelpOptions = true,
    description = "Simplifies grammar, and prints first and follow of each non-terminal")
class FirstFollow implements Callable<Integer> {

  @Parameters(index = "0")
  File file;

  @Option(names = "-o", defaultValue = "First-Follow.csv")
  String outFile;

  @Option(names = "-S", required = true)
  String startSymbol;

  @Option(names = "-p", description = "Pretty print grammar")
  boolean pretty;

  @Override
  public Integer call() throws IOException {
    InputStream inputStream = new FileInputStream(file);
    CharStream charStream = CharStreams.fromStream(inputStream);

    SimpLangLexer lexer = new SimpLangLexer(charStream);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    SimpLangParser parser = new SimpLangParser(tokenStream);
    ParseTree tree = parser.source();

    Driver compiler = new Driver();
    compiler.visit(tree);

    Map<String, List<List<String>>> simplifiedGrammar =
        Simplifications.leftFactor(
            Simplifications.removeLeftRecursion(compiler.getProductionRules()));
    Map<String, String> terminalAliases = compiler.getTerminalRules();
    terminalAliases.put("%empty", "ϵ");
    terminalAliases.put("%eof", "$");

    Map<String, Set<String>> firstSet = Simplifications.firstSet(simplifiedGrammar);
    Map<String, Set<String>> followSet = Simplifications.followSet(simplifiedGrammar, startSymbol);

    try (final CSVPrinter printer =
        new CSVPrinter(
            new FileWriter(outFile),
            CSVFormat.RFC4180.builder().setHeader("Nonterminal", "FIRST", "FOLLOW").build())) {
      for (String symbol : simplifiedGrammar.keySet()) {
        StringJoiner sj1 = new StringJoiner(",");
        StringJoiner sj2 = new StringJoiner(",");
        firstSet.get(symbol).stream()
            .map(
                firstSymbol ->
                    StringEscapeUtils.escapeCsv(
                        pretty
                            ? terminalAliases.getOrDefault(firstSymbol, firstSymbol)
                            : firstSymbol))
            .forEach(sj1::add);
        followSet.get(symbol).stream()
            .map(
                followSymbol ->
                    StringEscapeUtils.escapeCsv(
                        pretty
                            ? terminalAliases.getOrDefault(followSymbol, followSymbol)
                            : followSymbol))
            .forEach(sj2::add);
        printer.printRecord(symbol, sj1.toString(), sj2.toString());
      }
    }

    return 0;
  }
}

@Command(
    name = "ll-parser",
    mixinStandardHelpOptions = true,
    description = "Generates LL(1) parse table")
class Parser implements Callable<Integer> {

  @Parameters(index = "0")
  File file;

  @Option(names = "-S", required = true)
  String startSymbol;

  @Override
  public Integer call() throws IOException {
    InputStream inputStream = new FileInputStream(file);
    CharStream charStream = CharStreams.fromStream(inputStream);

    SimpLangLexer lexer = new SimpLangLexer(charStream);
    CommonTokenStream tokenStream = new CommonTokenStream(lexer);

    SimpLangParser parser = new SimpLangParser(tokenStream);
    ParseTree tree = parser.source();

    Driver compiler = new Driver();
    compiler.visit(tree);

    Map<String, List<List<String>>> simplifiedGrammar =
        Simplifications.leftFactor(
            Simplifications.removeLeftRecursion(compiler.getProductionRules()));

    ParseTable table = new ParseTable(simplifiedGrammar, startSymbol);

    System.out.printf(
        """
            #include <stddef.h>

            #define TERMINAL_COUNT %d
            #define NONTERMINAL_COUNT %d

            """,
        table.getTerminals().size(), table.getNonTerminals().size());

    ObjectMapper objectMapper = new ObjectMapper();

    for (String terminal : table.getTerminals()) {
      String terminalMacro = Simplifications.symbolMacro(terminal);
      String terminalRep =
          terminal.equals("%eof")
              ? "$"
              : compiler.getTerminalRules().containsKey(terminal)
                  ? objectMapper.writeValueAsString(compiler.getTerminalRules().get(terminal))
                  : terminal;

      System.out.printf("#define %s %s\n", terminalMacro, table.getSymbolToId().get(terminal));
      System.out.printf(
          "#define %s_STR %s\n", terminalMacro, objectMapper.writeValueAsString(terminalRep));
    }
    System.out.println();

    for (String nonTerminal : table.getNonTerminals()) {
      String nonTerminalMacro = Simplifications.symbolMacro(nonTerminal);
      System.out.printf(
          "#define %s %s\n", nonTerminalMacro, table.getSymbolToId().get(nonTerminal));
      System.out.printf(
          "#define %s_STR %s\n", nonTerminalMacro, objectMapper.writeValueAsString(nonTerminal));
    }

    System.out.println();

    StringJoiner symbolRepListJoiner =
        new StringJoiner(
            ",\n    ",
            String.format(
                "char *symbol_reps[%d] = {\n    ",
                table.getTerminals().size() + table.getNonTerminals().size()),
            "};\n");

    table.getTerminals().stream()
        .map(symbol -> Simplifications.symbolMacro(symbol) + "_STR")
        .forEachOrdered(symbolRepListJoiner::add);

    table.getNonTerminals().stream()
        .map(symbol -> Simplifications.symbolMacro(symbol) + "_STR")
        .forEachOrdered(symbolRepListJoiner::add);

    System.out.println(symbolRepListJoiner);

    for (int i = 0; i < table.getRuleList().size(); i++) {
      StringJoiner ruleJoiner =
          new StringJoiner(
              ", ",
              String.format("int rule%d[%d] = {", i, table.getRuleList().get(i).size() + 1),
              "};\n");

      List<String> reversedRule = new ArrayList<>(table.getRuleList().get(i));
      Collections.reverse(reversedRule);
      reversedRule.stream().map(Simplifications::symbolMacro).forEachOrdered(ruleJoiner::add);
      ruleJoiner.add("-1");
      System.out.printf("%s", ruleJoiner.toString());
    }

    StringJoiner tableJoiner =
        new StringJoiner(
            ",\n",
            String.format(
                "int *yytab[%d][%d] = {\n",
                table.getNonTerminals().size(), table.getTerminals().size()),
            ",\n};\n");
    for (List<Integer> row : table.getParseTable()) {
      StringJoiner rowJoiner = new StringJoiner(", ", "    {", "}");
      row.stream()
          .map(ruleId -> ruleId < 0 ? "NULL" : "rule" + ruleId)
          .forEachOrdered(rowJoiner::add);
      tableJoiner.add(rowJoiner.toString());
    }
    System.out.println();
    System.out.printf("%s", tableJoiner);
    return 0;
  }
}
