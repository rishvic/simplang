package net.rishvic.simplang;

import com.google.common.flogger.FluentLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import net.rishvic.simplang.analysis.Simplifications;
import net.rishvic.simplang.antlr.SimpLangLexer;
import net.rishvic.simplang.antlr.SimpLangParser;
import net.rishvic.simplang.compiler.Driver;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.jgrapht.graph.DefaultEdge;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "simplang",
    mixinStandardHelpOptions = true,
    version = "SimpLang v1.0.0-SNAPSHOT",
    description = "Parses & analyzes SimpLang language specifications.")
public class App implements Callable<Integer> {
  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

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

    System.out.println("Initial rules:");
    System.out.printf("%s", Simplifications.toPrettyString(compiler.getProductionRules()));

    Map<String, List<List<String>>> noLeftRecRuleset =
        Simplifications.removeLeftRecursion(compiler.getProductionRules());

    System.out.println("Rules after removing left recursion:");
    System.out.printf("%s", Simplifications.toPrettyString(noLeftRecRuleset));

    Map<String, List<List<String>>> simpleRuleset = Simplifications.leftFactor(noLeftRecRuleset);

    System.out.println("Rules after removing left factoring:");
    System.out.printf("%s", Simplifications.toPrettyString(simpleRuleset));

    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
