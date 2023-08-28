package net.rishvic.simplang;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.Callable;
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
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "simplang",
    mixinStandardHelpOptions = true,
    version = "SimpLang v1.0.0-SNAPSHOT",
    description = "Parses & analyzes SimpLang language specifications.",
    subcommands = {Simplify.class, FirstFollow.class})
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

    Map<String, Set<String>> firstSet = Simplifications.firstSet(simplifiedGrammar);

    try (final CSVPrinter printer =
        new CSVPrinter(
            new FileWriter(outFile),
            CSVFormat.RFC4180.builder().setHeader("Nonterminal", "FIRST").build())) {
      for (String symbol : simplifiedGrammar.keySet()) {
        StringJoiner sj = new StringJoiner(",");
        firstSet.get(symbol).forEach(sj::add);
        printer.printRecord(symbol, sj.toString());
      }
    }

    return 0;
  }
}
