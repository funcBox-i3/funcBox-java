package funcBox.Dijkstra;

import java.util.List;
import java.util.Map;

public class Result {
    public Map<String, Integer> distances;
    public Map<String, List<String>> paths;

    public Result(Map<String, Integer> distances, Map<String, List<String>> paths) {
        this.distances = distances;
        this.paths = paths;
    }
}
