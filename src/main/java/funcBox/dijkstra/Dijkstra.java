package funcBox.dijkstra;

import java.util.*;

/**
 * Utility class providing an implementation of
 * <a href="https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Dijkstra's Algorithm</a>
 * to compute the shortest paths in a weighted graph.
 *
 * <p>The graph must be represented as:
 * <pre>
 * Map&lt;String, Map&lt;String, Integer&gt;&gt;
 * </pre>
 *
 * where:
 * <ul>
 *     <li>Key = Node</li>
 *     <li>Value = Map of neighbor nodes and their edge weights</li>
 * </ul>
 *
 * <p>All edge weights must be non-negative.
 * Methods throw {@link IllegalArgumentException} when input validation fails.
 *
 * <p><b>Library:</b> funcBox
 *
 * @since 1.0.0
 */
public final class Dijkstra {

    /**
     * Prevents instantiation of this static utility class.
     */
    private Dijkstra() {}

    /**
     * Computes the shortest paths from the given start node and returns
     * the result covering all reachable nodes.
     *
     * <p>This method internally:
     * <ol>
     *     <li>Runs the Dijkstra algorithm from the start node</li>
     *     <li>Finds the farthest reachable node</li>
     *     <li>Returns the shortest path from the start node to that node</li>
     * </ol>
     *
     * @param graph     the graph represented as adjacency maps
     * @param startNode the starting node
     * @return a {@link Result} object containing distances and paths
     * @throws IllegalArgumentException if input is invalid or graph contains negative edge weights
     */
    public static Result dijkstra(Map<String, Map<String, Integer>> graph, String startNode) {
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

    /**
     * Computes the shortest path between a start node and an optional end node.
     *
     * <p>If {@code endNode} is {@code null}, shortest paths to all nodes are returned.
     * If an end node is provided, only the nodes along the shortest path
     * from the start node to the end node are returned.
     *
     * @param graph     the graph represented as adjacency maps
     * @param startNode the starting node
     * @param endNode   the destination node (optional)
     * @return a {@link Result} containing distances and computed paths
     * @throws IllegalArgumentException if nodes are invalid or any traversed edge has negative weight
     */
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
            if (neighbors == null) continue;

            for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                String neighbor = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                if (weight < 0) {
                    throw new IllegalArgumentException("Edge weights must be non-negative for Dijkstra.");
                }

                distances.putIfAbsent(neighbor, Integer.MAX_VALUE);
                paths.putIfAbsent(neighbor, null);

                long newDistLong = (long) distances.get(currentNode) + weight;
                int newDist = newDistLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newDistLong;

                if (newDist < distances.get(neighbor)) {
                    distances.put(neighbor, newDist);
                    List<String> newPath = new ArrayList<>(paths.get(currentNode));
                    newPath.add(neighbor);
                    paths.put(neighbor, newPath);
                    queue.offer(new Node(neighbor, newDist));
                }
            }
        }

        if (endNode != null) {
            Map<String, Integer> filteredDistances = new LinkedHashMap<>();
            Map<String, List<String>> filteredPaths = new LinkedHashMap<>();
            List<String> path = paths.get(endNode);
            if (path != null) {
                for (String node : path) {
                    filteredDistances.put(node, distances.get(node));
                    filteredPaths.put(node, paths.get(node));
                }
            }
            return new Result(filteredDistances, filteredPaths);
        }
        return new Result(distances, paths);
    }

    /**
     * Internal helper class used in the priority queue.
     */
    static class Node {
        /** Node identifier. */
        String name;
        /** Current best-known tentative distance. */
        int distance;

        Node(String name, int distance) {
            this.name = name;
            this.distance = distance;
        }
    }
}
