package net.rishvic.simplang.analysis;

import com.google.common.flogger.FluentLogger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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

  public static Map<String, List<List<String>>> leftFactor(Map<String, List<List<String>>> rules) {
    Map<String, TrieNode> reductions = new HashMap<>();

    for (Map.Entry<String, List<List<String>>> entry : rules.entrySet()) {
      String nonTerminal = entry.getKey();
      List<List<String>> ruleset = entry.getValue();

      TrieNode root = new TrieNode();
      for (List<String> rule : ruleset) {
        root.addRule(rule);
      }
      root.reduce(reductions, nonTerminal + "-");

      entry.setValue(root.genRuleset(new ArrayList<>()));
    }

    for (Map.Entry<String, TrieNode> entry : reductions.entrySet()) {
      String nonTerminal = entry.getKey();
      TrieNode node = entry.getValue();

      rules.put(nonTerminal, node.genRuleset(new ArrayList<>()));
    }

    return rules;
  }

  public static String toPrettyString(Map<String, List<List<String>>> rules) {
    StringBuilder sb = new StringBuilder();

    if (rules.isEmpty()) {
      return "ϕ\n";
    }

    for (Map.Entry<String, List<List<String>>> entry : rules.entrySet()) {
      String nonTerminal = entry.getKey();
      List<List<String>> ruleset = entry.getValue();

      if (ruleset.isEmpty()) {
        sb.append(nonTerminal).append(" -> ϕ\n");
        continue;
      }

      if (nonTerminal.length() == 1) {
        sb.append(nonTerminal).append(" ->");
      } else {
        sb.append(nonTerminal).append("\n  ->");
      }

      for (int i = 0; i < ruleset.size(); i++) {
        if (ruleset.get(i).isEmpty()) {
          sb.append(" ϵ");
        } else {
          for (String symbol : ruleset.get(i)) {
            sb.append(' ').append(symbol);
          }
        }

        if (i == ruleset.size() - 1) {
          sb.append('\n').append("   ;\n\n");
        } else {
          sb.append('\n').append("   |");
        }
      }
    }

    return sb.toString();
  }

  private static class TrieNode {
    int out;
    Map<String, TrieNode> child;

    public TrieNode() {
      out = 0;
      child = new HashMap<>();
    }

    void addRule(List<String> rule) {
      TrieNode node = this;
      for (String symbol : rule) {
        if (!node.child.containsKey(symbol)) {
          TrieNode newNode = new TrieNode();
          node.child.put(symbol, newNode);
          node = newNode;
        } else {
          node = node.child.get(symbol);
        }
      }

      node.out++;
    }

    void reduce(Map<String, TrieNode> reductions, String prefix) {
      for (Map.Entry<String, TrieNode> entry : child.entrySet()) {
        String childSymbol = entry.getKey();
        TrieNode childNode = entry.getValue();
        String childPrefix = prefix + childSymbol;

        childNode.reduce(reductions, childPrefix + '.');
        if (childNode.child.size() + childNode.out <= 1) continue;

        reductions.put(childPrefix, childNode);

        TrieNode newChildNode = new TrieNode();
        newChildNode.addRule(List.of(childPrefix));
        entry.setValue(newChildNode);
      }
    }

    List<List<String>> genRuleset(List<String> prefix) {
      List<List<String>> ruleset = new ArrayList<>();
      if (out > 0) {
        ruleset.add(new ArrayList<>(prefix));
      }

      for (Map.Entry<String, TrieNode> entry : child.entrySet()) {
        String childSymbol = entry.getKey();
        TrieNode childNode = entry.getValue();
        prefix.add(childSymbol);

        ruleset.addAll(childNode.genRuleset(prefix));

        prefix.remove(prefix.size() - 1);
      }

      return ruleset;
    }
  }
}
