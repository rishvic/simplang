package net.rishvic.simplang.analysis;

import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.alg.connectivity.KosarajuStrongConnectivityInspector;
import org.jgrapht.alg.interfaces.StrongConnectivityAlgorithm;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class Simplifications {

  private static final FluentLogger logger = FluentLogger.forEnclosingClass();

  public static boolean isNonTerminal(String symbol) {
    return !symbol.isEmpty() && Character.isUpperCase(symbol.charAt(0));
  }

  public static boolean isTerminal(String symbol) {
    return !symbol.isEmpty() && Character.isLowerCase(symbol.charAt(0));
  }

  public static Map<String, List<List<String>>> removeLeftRecursion(
      Map<String, List<List<String>>> rules) {
    Map<String, List<List<String>>> oldRules;
    Map<String, List<List<String>>> newRules = new HashMap<>(rules);

    do {
      oldRules = newRules;
      newRules = new HashMap<>();

      // Step 0: Get topological order of the symbols.
      Graph<String, DefaultEdge> firstSymGraph = new DefaultDirectedGraph<>(DefaultEdge.class);
      for (String s : oldRules.keySet()) {
        firstSymGraph.addVertex(s);
      }

      for (Map.Entry<String, List<List<String>>> entry : oldRules.entrySet()) {
        for (List<String> rule : entry.getValue()) {
          if (rule.isEmpty()) continue;
          String firstSymbol = rule.get(0);
          if (isNonTerminal(firstSymbol)) {
            firstSymGraph.addEdge(entry.getKey(), firstSymbol);
          }
        }
      }

      StrongConnectivityAlgorithm<String, DefaultEdge> scAlg =
          new KosarajuStrongConnectivityInspector<>(firstSymGraph);
      List<Graph<String, DefaultEdge>> components = scAlg.getStronglyConnectedComponents();

      List<String> symbols = new ArrayList<>(oldRules.size());
      for (Graph<String, DefaultEdge> component : components) {
        symbols.addAll(component.vertexSet());
      }

      Map<String, Integer> symbolToId = new HashMap<>();
      for (int i = 0; i < symbols.size(); i++) {
        symbolToId.put(symbols.get(i), i);
      }

      // Step 1: Resolve rules which point to smaller id symbols.
      for (String nonTerm : symbols) {
        List<List<String>> ruleset = new ArrayList<>();
        newRules.put(nonTerm, ruleset);

        for (List<String> rule : oldRules.get(nonTerm)) {
          if (rule.isEmpty()) {
            ruleset.add(rule);
            continue;
          }

          String firstSymbol = rule.get(0);
          if (!isNonTerminal(firstSymbol)
              || symbolToId.get(firstSymbol) >= symbolToId.get(nonTerm)) {
            ruleset.add(rule);
            continue;
          }

          for (List<String> fsRule : newRules.get(firstSymbol)) {
            List<String> newRule = new ArrayList<>(fsRule);
            int ruleSize = rule.size();
            newRule.addAll(rule.subList(1, ruleSize));
            ruleset.add(newRule);
          }
        }
      }

      Map<String, List<List<String>>> finalRules = new HashMap<>();

      // Step 2: Get rid of direct recursion.
      for (Map.Entry<String, List<List<String>>> entry : newRules.entrySet()) {
        String nonTerm = entry.getKey();
        List<List<String>> ruleset = entry.getValue();
        ruleset.remove(List.of(nonTerm));

        List<List<String>> aRuleset =
            ruleset.stream()
                .filter(rule -> !rule.isEmpty() && rule.get(0).equals(nonTerm))
                .toList();

        List<List<String>> bRuleset =
            ruleset.stream()
                .filter(rule -> rule.isEmpty() || !rule.get(0).equals(nonTerm))
                .toList();

        if (aRuleset.isEmpty()) {
          finalRules.put(nonTerm, ruleset);
          continue;
        }

        String newNonTerm = nonTerm + "_r";

        List<List<String>> existingSet = new ArrayList<>(bRuleset);
        List<List<String>> newTermSet = new ArrayList<>(aRuleset);
        if (bRuleset.isEmpty()) {
          logger.atWarning().log("Infinite recursion in rule %s -> %s", nonTerm, ruleset);
          existingSet.add(new ArrayList<>(List.of(newNonTerm)));
        } else {
          for (List<String> bRule : existingSet) {
            bRule.add(newNonTerm);
          }
        }
        for (List<String> aRule : newTermSet) {
          aRule.remove(0);
          aRule.add(newNonTerm);
        }
        if (!bRuleset.isEmpty()) {
          newTermSet.add(new ArrayList<>());
        }

        finalRules.put(nonTerm, existingSet);
        finalRules.put(newNonTerm, newTermSet);
      }

      newRules = finalRules;
    } while (!oldRules.equals(newRules));

    return newRules;
  }
}
