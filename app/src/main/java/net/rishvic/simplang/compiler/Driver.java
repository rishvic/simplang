package net.rishvic.simplang.compiler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.rishvic.simplang.antlr.SimpLangBaseVisitor;
import net.rishvic.simplang.antlr.SimpLangParser.ConcatExprContext;
import net.rishvic.simplang.antlr.SimpLangParser.EmptyContext;
import net.rishvic.simplang.antlr.SimpLangParser.NonTermContext;
import net.rishvic.simplang.antlr.SimpLangParser.OrExprContext;
import net.rishvic.simplang.antlr.SimpLangParser.ParensContext;
import net.rishvic.simplang.antlr.SimpLangParser.ProductionRuleContext;
import net.rishvic.simplang.antlr.SimpLangParser.TermContext;
import net.rishvic.simplang.antlr.SimpLangParser.TerminalRuleContext;

public class Driver extends SimpLangBaseVisitor<Optional<List<List<String>>>> {
  private final Map<String, String> terminalRules = new HashMap<>();
  private final Map<String, List<List<String>>> productionRules = new HashMap<>();

  public Map<String, String> getTerminalRules() {
    return terminalRules;
  }

  public Map<String, List<List<String>>> getProductionRules() {
    return productionRules;
  }

  @Override
  public Optional<List<List<String>>> visitEmpty(EmptyContext ctx) {
    List<List<String>> rules = new ArrayList<>(Collections.singletonList(new ArrayList<>()));
    return Optional.of(rules);
  }

  @Override
  public Optional<List<List<String>>> visitTerm(TermContext ctx) {
    List<List<String>> rules =
        new ArrayList<>(
            Collections.singletonList(
                new ArrayList<>(Collections.singletonList(ctx.TERM().getText()))));
    return Optional.of(rules);
  }

  @Override
  public Optional<List<List<String>>> visitNonTerm(NonTermContext ctx) {
    List<List<String>> rules =
        new ArrayList<>(
            Collections.singletonList(
                new ArrayList<>(Collections.singletonList(ctx.NON_TERM().getText()))));
    return Optional.of(rules);
  }

  @Override
  public Optional<List<List<String>>> visitParens(ParensContext ctx) {
    return visit(ctx.expr());
  }

  @Override
  public Optional<List<List<String>>> visitOrExpr(OrExprContext ctx) {
    List<List<String>> leftRules = visit(ctx.left).orElseThrow();
    List<List<String>> rightRules = visit(ctx.right).orElseThrow();
    leftRules.addAll(rightRules);

    return Optional.of(leftRules);
  }

  @Override
  public Optional<List<List<String>>> visitConcatExpr(ConcatExprContext ctx) {
    List<List<String>> leftRules = visit(ctx.left).orElseThrow();
    List<List<String>> rightRules = visit(ctx.right).orElseThrow();

    List<List<String>> concatRules = new ArrayList<>();
    for (List<String> leftRule : leftRules) {
      for (List<String> rightRule : rightRules) {
        List<String> newRule = new ArrayList<>(leftRule);
        newRule.addAll(rightRule);
        concatRules.add(newRule);
      }
    }

    return Optional.of(concatRules);
  }

  @Override
  public Optional<List<List<String>>> visitProductionRule(ProductionRuleContext ctx) {
    String nonTerm = ctx.NON_TERM().getText();
    List<List<String>> rules = visit(ctx.expr()).orElseThrow();
    List<List<String>> existingRules = productionRules.putIfAbsent(nonTerm, rules);
    if (Objects.nonNull(existingRules)) {
      existingRules.addAll(rules);
    }

    return Optional.empty();
  }

  @Override
  public Optional<List<List<String>>> visitTerminalRule(TerminalRuleContext ctx) {
    String term = ctx.TERM().getText();
    String string = ctx.STRING().getText();
    int strLen = string.length();
    string = string.substring(1, strLen - 1);

    terminalRules.put(term, string);
    return Optional.empty();
  }
}
