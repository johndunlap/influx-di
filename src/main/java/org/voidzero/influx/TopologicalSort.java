package org.voidzero.influx;

import java.util.*;

public class TopologicalSort<T> {
    private final Map<T, List<T>> graph;
    private final List<T> topologicalOrder;
    private final Set<T> visited;
    private final Set<T> recursionStack;
    private boolean cycleDetected;
    private final List<T> cycleNodes;

    public TopologicalSort(Map<T, List<T>> graph) {
        this.graph = graph;
        this.topologicalOrder = new ArrayList<>();
        this.visited = new HashSet<>();
        this.recursionStack = new HashSet<>();
        this.cycleDetected = false;
        this.cycleNodes = new ArrayList<>();
    }

    public List<T> topologicalSort() {
        for (T vertex : graph.keySet()) {
            if (!visited.contains(vertex)) {
                if (dfs(vertex)) {
                    cycleDetected = true;
                    break;
                }
            }
        }

        if (cycleDetected) {
            return cycleNodes;
        } else {
            Collections.reverse(topologicalOrder);
            return topologicalOrder;
        }
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
                cycleNodes.add(neighbor);
                T current = vertex;
                while (!current.equals(neighbor)) {
                    cycleNodes.add(current);
                    current = recursionStack.stream().findFirst().orElse(null);
                }
                cycleNodes.add(neighbor);
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
        if (topologicalSort.cycleDetected) {
            System.out.println("Cycle detected, dependency chain:");
        } else {
            System.out.println("Topological order:");
        }
        System.out.println(result);
    }
}
