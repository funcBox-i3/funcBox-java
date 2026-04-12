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
     * <p>This method runs the Dijkstra algorithm from the start node and
     * filters out unreachable nodes before returning the result.</p>
     *
     * @param graph     the graph represented as adjacency maps
     * @param startNode the starting node
     * @return a {@link Result} object containing distances and paths
     * @throws IllegalArgumentException if input is invalid or graph contains negative edge weights
     */
    public static Result dijkstra(Map<String, Map<String, Integer>> graph, String startNode) {
        Result all = runDijkstra(graph, startNode, null);

        Map<String, Integer> reachableDistances = new LinkedHashMap<>();
        Map<String, List<String>> reachablePaths = new LinkedHashMap<>();
        for (Map.Entry<String, Integer> entry : all.distances.entrySet()) {
            Integer distance = entry.getValue();
            if (distance != null && distance != Integer.MAX_VALUE) {
                String node = entry.getKey();
                reachableDistances.put(node, distance);
                reachablePaths.put(node, all.paths.get(node));
            }
        }

        return new Result(reachableDistances, reachablePaths);
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
        return runDijkstra(graph, startNode, endNode);
    }

    private static Result runDijkstra(Map<String, Map<String, Integer>> graph, String startNode, String endNode) {
        if (graph == null || !graph.containsKey(startNode)) {
            throw new IllegalArgumentException("Start node must be in the graph.");
        }
        if (endNode != null && !graph.containsKey(endNode)) {
            throw new IllegalArgumentException("End node must be in the graph.");
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        for (String node : graph.keySet()) {
            distances.put(node, Integer.MAX_VALUE);
        }

        distances.put(startNode, 0);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));
        queue.offer(new Node(startNode, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            String u = current.name;

            if (current.distance > distances.get(u)) {
                continue;
            }

            if (u.equals(endNode)) break;

            Map<String, Integer> neighbors = graph.get(u);
            if (neighbors == null) continue;

            for (Map.Entry<String, Integer> neighborEntry : neighbors.entrySet()) {
                String v = neighborEntry.getKey();
                int weight = neighborEntry.getValue();
                if (weight < 0) {
                    throw new IllegalArgumentException("Edge weights must be non-negative for Dijkstra.");
                }

                long newDistLong = (long) distances.get(u) + weight;
                int newDist = newDistLong > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) newDistLong;

                if (newDist < distances.getOrDefault(v, Integer.MAX_VALUE)) {
                    distances.put(v, newDist);
                    predecessors.put(v, u);
                    queue.offer(new Node(v, newDist));
                }
            }
        }

        if (endNode != null) {
            Map<String, Integer> filteredDistances = new LinkedHashMap<>();
            Map<String, List<String>> filteredPaths = new LinkedHashMap<>();
            List<String> path = reconstructPath(predecessors, startNode, endNode);
            if (path != null) {
                for (String node : path) {
                    filteredDistances.put(node, distances.get(node));
                    filteredPaths.put(node, reconstructPath(predecessors, startNode, node));
                }
            }
            return new Result(filteredDistances, filteredPaths);
        }

        Map<String, List<String>> allPaths = new HashMap<>();
        for (String node : distances.keySet()) {
            if (distances.get(node) != Integer.MAX_VALUE) {
                allPaths.put(node, reconstructPath(predecessors, startNode, node));
            }
        }
        return new Result(distances, allPaths);
    }

    private static List<String> reconstructPath(Map<String, String> predecessors, String start, String end) {
        if (!predecessors.containsKey(end) && !start.equals(end)) {
            return null;
        }
        LinkedList<String> path = new LinkedList<>();
        for (String at = end; at != null; at = predecessors.get(at)) {
            path.addFirst(at);
            if (at.equals(start)) break;
        }
        return path;
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
