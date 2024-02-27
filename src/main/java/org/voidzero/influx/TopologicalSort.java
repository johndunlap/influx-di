package org.voidzero.influx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TopologicalSort<T> {
    private final Map<T, List<T>> graph;
    private final List<T> topologicalOrder;
    private final Set<T> visited;
    private final Set<T> recursionStack;

    public TopologicalSort(Map<T, List<T>> graph) {
        this.graph = graph;
        this.topologicalOrder = new ArrayList<>();
        this.visited = new HashSet<>();
        this.recursionStack = new HashSet<>();
    }

    public List<T> topologicalSort() {
        for (T vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                if (dfs(vertex)) {
                    throw new IllegalArgumentException("The graph contains cycles");
                }
            }
        }

        Collections.reverse(topologicalOrder);
        return topologicalOrder;
    }

    private boolean dfs(T vertex) {
        visited.add(vertex);
        recursionStack.add(vertex);

        List<T> neighbors = graph.getOrDefault(vertex, Collections.emptyList());
        for (T neighbor : neighbors) {
            if (!visited.contains(neighbor)) {
                if (dfs(neighbor)) {
                    return true;
                }
            } else if (recursionStack.contains(neighbor)) {
                return true; // Cycle detected
            }
        }

        recursionStack.remove(vertex);
        topologicalOrder.add(vertex);
        return false;
    }

    public static void main(String[] args) {
        // Example graph represented as an adjacency list
        Map<String, List<String>> graph = new HashMap<>();
        graph.put("A", Arrays.asList("B", "C"));
        graph.put("B", Collections.singletonList("D"));
        graph.put("C", Arrays.asList("D", "E"));
        graph.put("D", Collections.singletonList("E"));
        graph.put("E", Collections.emptyList());
        graph.put("F", Collections.singletonList("A"));

        TopologicalSort<String> topologicalSort = new TopologicalSort<>(graph);
        List<String> result = topologicalSort.topologicalSort();
        System.out.println(result);
    }
}