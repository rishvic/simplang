package net.rishvic.simplang.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParseTable {
  private final Map<String, List<List<String>>> rules;
  private final List<List<Integer>> parseTable;
  private final List<String> nonTerminals;
  private final List<String> terminals;

  private final Map<String, Integer> symbolToId;
  private final String startSymbol;
  private final List<List<String>> ruleList;

  public ParseTable(Map<String, List<List<String>>> rules, String startSymbol)
      throws IllegalArgumentException {
    this.rules = rules;
    this.parseTable = new ArrayList<>(rules.size());
    this.nonTerminals = new ArrayList<>(rules.keySet());

    this.terminals = new ArrayList<>();
    Set<String> terminalsSet = new HashSet<>();
    this.rules
        .values()
        .forEach(
            ruleset ->
                ruleset.forEach(
                    rule ->
                        terminalsSet.addAll(
                            rule.stream().filter(Simplifications::isTerminal).toList())));
    this.terminals.addAll(terminalsSet);
    this.terminals.add("%eof");

    this.symbolToId = new HashMap<>();
    this.startSymbol = startSymbol;
    this.ruleList = new ArrayList<>();
    generateTable();
  }

  private void generateTable() throws IllegalArgumentException {
    Map<String, Integer> terminalToId = new HashMap<>();
    for (int i = 0; i < terminals.size(); i++) {
      terminalToId.put(terminals.get(i), i);
      symbolToId.put(terminals.get(i), i);
    }

    Map<String, Set<String>> firstSet = Simplifications.firstSet(rules);
    Map<String, Set<String>> followSet = Simplifications.followSet(rules, startSymbol);

    for (String nonTerminal : nonTerminals) {
      List<List<String>> ruleset = rules.get(nonTerminal);

      int nonTermId = parseTable.size();
      parseTable.add(new ArrayList<>(Collections.nCopies(terminals.size(), -1)));
      symbolToId.put(nonTerminal, terminals.size() + nonTermId);

      for (List<String> rule : ruleset) {
        int ruleId = ruleList.size();
        ruleList.add(rule);

        Set<String> terms = Simplifications.firstSet(rule, firstSet);
        if (terms.contains("%empty")) {
          terms.addAll(followSet.get(nonTerminal));
          terms.remove("%empty");
        }

        for (String terminal : terms) {
          int columnNo = terminalToId.get(terminal);
          if (parseTable.get(nonTermId).get(columnNo) != -1) {
            throw new IllegalArgumentException("Conflict in grammar");
          }
          parseTable.get(nonTermId).set(columnNo, ruleId);
        }
      }
    }
  }

  public Map<String, List<List<String>>> getRules() {
    return rules;
  }

  public List<List<Integer>> getParseTable() {
    return parseTable;
  }

  public List<String> getNonTerminals() {
    return nonTerminals;
  }

  public List<String> getTerminals() {
    return terminals;
  }

  public Map<String, Integer> getSymbolToId() {
    return symbolToId;
  }

  public String getStartSymbol() {
    return startSymbol;
  }

  public List<List<String>> getRuleList() {
    return ruleList;
  }
}
