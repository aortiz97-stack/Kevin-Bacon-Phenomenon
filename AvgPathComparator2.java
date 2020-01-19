import java.util.Comparator;
import java.util.Map;
import java.util.Set;
/**
 * Comparator designed to help sort actors by average path lengths from greatest to least 
 * @author Armando Ortiz, Dartmouth College, CS10, Fall 2018
 *
 */
public class AvgPathComparator2 implements Comparator<String> {
	private Graph<String, Set<String>> graph;
	
	public AvgPathComparator2(Graph<String, Set<String>> graph) {
		this.graph = graph;
	}
	private Double getAvgPath(String actorName) {
		Graph<String, Set<String>> shortestPath = GraphLib.bfs(graph, actorName);
		return GraphLib.averageSeparation(shortestPath, actorName);
	}
	@Override
	public int compare(String a1, String a2) {
		if (getAvgPath(a1)>getAvgPath(a2)) {
			return -1;
		}
		else if (getAvgPath(a1)==getAvgPath(a2)) {
			return 0;
		}
		else {
			return 1;
		}
	}
}
