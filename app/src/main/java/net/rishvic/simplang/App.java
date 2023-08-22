/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package net.rishvic.simplang;

import java.util.concurrent.Callable;
import picocli.CommandLine;
import picocli.CommandLine.Command;

@Command(
    name = "simplang",
    mixinStandardHelpOptions = true,
    version = "SimpLang v1.0.0-SNAPSHOT",
    description = "Parses & analyzes SimpLang language specifications.")
public class App implements Callable<Integer> {
  public String getGreeting() {
    return "Hello World!";
  }

  @Override
  public Integer call() throws Exception {
    System.out.println(new App().getGreeting());
    return null;
  }

  public static void main(String[] args) {
    int exitCode = new CommandLine(new App()).execute(args);
    System.exit(exitCode);
  }
}
