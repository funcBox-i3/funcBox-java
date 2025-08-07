package funcBox.Dijkstra;

import java.util.*;

public class dijkstra {

    public static Result dijkstra(Map<String, Map<String, Integer>> graph, String startNode) {
        // Find the farthest node from startNode
        Result all = dijkstra(graph, startNode, null);
        String farthest = null;
        int maxDist = Integer.MIN_VALUE;
        for (Map.Entry<String, Integer> entry : all.distances.entrySet()) {
            if (entry.getValue() != Integer.MAX_VALUE && entry.getValue() > maxDist) {
                maxDist = entry.getValue();
                farthest = entry.getKey();
            }
        }
        if (farthest != null && !farthest.equals(startNode)) {
            return dijkstra(graph, startNode, farthest);
        }
        return all;
    }

    public static Result dijkstra(Map<String, Map<String, Integer>> graph, String startNode, String endNode) {
        if (graph == null || !graph.containsKey(startNode)) {
            throw new IllegalArgumentException("Start node must be in the graph.");
        }
        if (endNode != null && !graph.containsKey(endNode)) {
            throw new IllegalArgumentException("End node must be in the graph.");
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, List<String>> paths = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
            paths.put(node, null);
        }

        distances.put(startNode, 0);
        paths.put(startNode, new ArrayList<>(List.of(startNode)));

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        queue.offer(new Node(startNode, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            String currentNode = current.name;

            // Skip outdated entries
            if (current.distance > distances.get(currentNode)) {
                continue;
            }

            Map<String, Integer> neighbors = graph.get(currentNode);
            if (neighbors == null)
                continue;
            for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                int newDist = distances.get(currentNode) + weight;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    List<String> newPath = new ArrayList<>(paths.get(currentNode));
                    newPath.add(neighbor);
                    paths.put(neighbor, newPath);
                    queue.offer(new Node(neighbor, newDist));
                }
            }
        }

        // After the loop
        if (endNode != null) {
            // Only include nodes on the path from start to endNode and their predecessors
            Map<String, Integer> filteredDistances = new LinkedHashMap<>();
            Map<String, List<String>> filteredPaths = new LinkedHashMap<>();
            List<String> path = paths.get(endNode);
            if (path != null) {
                for (int i = 0; i < path.size(); i++) {
                    String node = path.get(i);
                    filteredDistances.put(node, distances.get(node));
                    filteredPaths.put(node, paths.get(node));
                }
            }
            return new Result(filteredDistances, filteredPaths);
        }
        return new Result(distances, paths);
    }

    static class Node {
        String name;
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}
