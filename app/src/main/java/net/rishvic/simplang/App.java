package net.rishvic.simplang;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.Callable;
import net.rishvic.simplang.antlr.SimpLangLexer;
import net.rishvic.simplang.antlr.SimpLangParser;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
    name = "simplang",
    mixinStandardHelpOptions = true,
    version = "SimpLang v1.0.0-SNAPSHOT",
    description = "Parses & analyzes SimpLang language specifications.")
public class App implements Callable<Integer> {

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

    System.out.println(tree.toStringTree(parser));

    return 0;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
