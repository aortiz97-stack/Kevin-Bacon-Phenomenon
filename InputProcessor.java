import java.awt.List;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Class to read the files of actors and movies
 * Creates a graph
 * @author Armando Ortiz Jr.
 *
 */
public class InputProcessor {
	private Graph<String, Set<String>> actorMovieGraph;
	private String actorFileName;
	private String movieFileName;
	private String movieActorFileName;
	
	public InputProcessor(String actorFileName, String movieFileName, String movieActorFileName) {
		this.actorFileName = actorFileName;
		this.movieFileName = movieFileName;
		this.movieActorFileName = movieActorFileName;
		
		actorMovieGraph = new AdjacencyMapGraph<String, Set<String>>();
	}
	
	/**
	 * Method reads the actor file, and creates a map of actor id as keys, and actor names as values
	 * @return idActorMap; map of actor id as keys and actor names as values
	 *  
	 */
	public Map<String, String> readActorFile()  {
		Map<String, String> idActorMap = new HashMap<String,String>();
		try {
			BufferedReader actorInput = new BufferedReader(new FileReader(actorFileName));
			String line;
			while ((line = actorInput.readLine()) != null) {
				String[]pieces = line.split("\\|");
				idActorMap.put(pieces[0], pieces[1]);
			}
			actorInput.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("Actor File not found " + e);
		} 
		catch (IOException e) {
			System.out.println("Actor File is empty " + e);
		}
		
		return idActorMap;
	}
	
	/**
	 * Method reads the movie file, and creates a map of movie id as keys, and movie names as values
	 * @return idMovieMap; map of movie id as keys and movie names as values
	 */
	public Map<String, String> readMovieFile(){
		Map<String, String> idMovieMap = new HashMap<String,String>();
		try {
			BufferedReader movieInput = new BufferedReader(new FileReader(movieFileName));
			String line;
			while ((line = movieInput.readLine()) != null) {
				String[]pieces = line.split("\\|");
				idMovieMap.put(pieces[0], pieces[1]);
			}
			movieInput.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("Movie File not found " + e);
		}
		catch (IOException e) {
			System.out.println("File is empty " + e);
		}
		return idMovieMap;
	}
	
	/**
	 * Method reads the movie-actor file, and creates a map of actor names as keys, and set of movie names as values
	 * @return actorMovieMap; map of actor names as keys and set of movie names as values
	 */
	public Map<String, Set<String>> readMovieActorFile(){
		Map<String, Set<String>> actorMovieMap = new HashMap<String,Set<String>>();
		Map<String, String> idActorMap = readActorFile();
		Map<String, String> idMovieMap = readMovieFile();
		try {
			BufferedReader movieActorInput = new BufferedReader(new FileReader(movieActorFileName));
			
			String line;
			String currentActorID = " ";
			//Read the file line by line
			while ((line = movieActorInput.readLine()) != null) {
				String[]pieces = line.split("\\|");
				
				//Add items to actorMovieMap
				if (!currentActorID.equals(pieces[1])) {
					//Add an empty set to map, which will have elements added later
					if (!actorMovieMap.containsKey(idActorMap.get(pieces[1]))){
						actorMovieMap.put(idActorMap.get(pieces[1]), new HashSet<String>());
					}
					//Reset currentActor and movieSet
					currentActorID = pieces[1];
				}
				//Continually add movies to set that corresponds to that one actor
				actorMovieMap.get(idActorMap.get(currentActorID)).add(idMovieMap.get(pieces[0]));	
			}
			movieActorInput.close();
		} 
		catch (FileNotFoundException e) {
			System.out.println("Movie-Actor File not found " + e);
		}
		catch (IOException e) {
			System.out.println("File is empty " + e);
		}
		return actorMovieMap;
	}
	
	/**
	 * Method returns a list of actor names in the graph
	 * @return actorList;
	 */
	public ArrayList<String> getActorList(){
		ArrayList<String> actorList = new ArrayList<String>();
		Set<String> actorSet = readMovieActorFile().keySet();
		
		for (String actor: actorSet) {
			actorList.add(actor);
		}
		return actorList;
	}
	
	/**
	 * Method creates the graph
	 */
	public void createGraph(){
		Map<String, Set<String>> actorMovieMap = readMovieActorFile();
		ArrayList<String> actorList = getActorList();
		Set<String> inCommonMovieSet = new HashSet<String>();	
	
		for (int i = 0; i <actorList.size(); i++) {
			for (int j = 1; j <actorList.size(); j++){
				String actor1 = actorList.get(i);
				String actor2 = actorList.get(j);
				actorMovieGraph.insertVertex(actor1);
				actorMovieGraph.insertVertex(actor2);
				
				Set<String> movieSet1 = actorMovieMap.get(actor1);
				Set<String> movieSet2 = actorMovieMap.get(actor2);
				
				for (String movie1: movieSet1) {
					
						for (String movie2: movieSet2) {
							if (movie1.equals(movie2)) {
								inCommonMovieSet.add(movie1);
							}
						}
						if (inCommonMovieSet.isEmpty()) {
							while (inCommonMovieSet.isEmpty() && j<actorList.size()-1) {
								j ++; //Move on to next actor using index j, and keep index i the same
								
								actor2 = actorList.get(j);
								movieSet2 = actorMovieMap.get(actor2);
								actorMovieGraph.insertVertex(actor2);
								Set <String>tempMovieSet1 = actorMovieMap.get(actor1); 
								
								for (String tempMovie1: tempMovieSet1) {
									for (String tempMovie2: movieSet2) {
										if (tempMovie1.equals(tempMovie2)) {
											inCommonMovieSet.add(tempMovie1);
										}
									}
								}
							}
						}
				}
				//Once we are done looking for any possible actor with same movie in entire list using index j, reset search to original conditions
				if (!inCommonMovieSet.isEmpty()&& !actor1.equals(actor2)) {
					actorMovieGraph.insertUndirected(actor1, actor2, inCommonMovieSet);
				}
				inCommonMovieSet = new HashSet<String>();
			}
		}
	}
	
	/**
	 * Method returns the graph of the input processor
	 * @return Graph<String, Set<String>> graph; the overall graph of actors
	 */
	public Graph<String, Set<String>> getGraph(){
		return actorMovieGraph;
	}
	
	/**
	 * Test files
	 */
	public static void main(String[]args) {
		InputProcessor test = new InputProcessor("actorsTest.txt", "moviesTest.txt","movie-actorsTest.txt");
		test.createGraph();
		Graph<String, Set<String>> tree = GraphLib.bfs(test.getGraph(), "Kevin Bacon"); // shortest pathTree
		System.out.println("Full graph \n" +test.getGraph()); // fullGraph
		System.out.println("\nShortest path " + "\n"+tree);
		System.out.println("\nFrom Charlie to Kevin Bacon: "+GraphLib.getPath(tree, "Charlie"));
		System.out.println("\nHere are the missing vertices: "+GraphLib.missingVertices(test.getGraph(), tree));
		System.out.println("\nAverage path length " + GraphLib.averageSeparation(tree, "Kevin Bacon"));
		
		System.out.println("\nBoundary cases");
		InputProcessor test2 = new InputProcessor("actorsTest.txt", "moviesTest.txt","movie-actorsTest.txt");
		test2.createGraph();
		Graph<String, Set<String>> tree2 = GraphLib.bfs(test2.getGraph(), "Nobody"); // shortest pathTree
		System.out.println("Full graph \n" +test2.getGraph()); // fullGraph
		System.out.println("\nShortest path " + "\n"+tree2);
		System.out.println("\nFrom Nobody's Friend to Nobody: "+GraphLib.getPath(tree2, "Nobody's Friend"));
		System.out.println("\nHere are the missing vertices: "+GraphLib.missingVertices(test2.getGraph(), tree2));
		System.out.println("\nAverage path length " + GraphLib.averageSeparation(tree2, "Nobody"));
		
	}
}
