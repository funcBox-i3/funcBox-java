package funcBox.dijkstra;

import java.util.List;
import java.util.Map;

/**
 * Represents the result produced by the {@link Dijkstra} algorithms.
 *
 * <p>It contains:
 * <ul>
 *     <li>Distances from the start node to each reachable node</li>
 *     <li>The computed path for each node</li>
 * </ul>
 *
 * <p><b>Library:</b> funcBox
 *
 * @since 1.0.0
 */
public class Result {

    /**
     * Shortest known distances keyed by node id.
     *
     * <p>Unreachable nodes may be represented by {@link Integer#MAX_VALUE} depending on call mode.</p>
     */
    public Map<String, Integer> distances;

    /**
     * Reconstructed paths keyed by destination node id.
     *
     * <p>Each path is represented as an ordered node-id list from source to key node.</p>
     */
    public Map<String, List<String>> paths;

    /**
     * Creates a result object containing distances and paths.
     *
     * @param distances shortest distances from the start node
     * @param paths     computed paths to each node
     */
    public Result(Map<String, Integer> distances, Map<String, List<String>> paths) {
        this.distances = distances;
        this.paths = paths;
    }
}
