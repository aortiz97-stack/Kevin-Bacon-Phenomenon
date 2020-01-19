import java.awt.List;
import java.util.*;

/**
 * 
 * @author Armando Ortiz
 *
 */
public class GameInterface {
	private InputProcessor engine; // input processor from which we will extract graph from 
	private String universe; //universe that we are looking for
	
	public GameInterface(InputProcessor engine) {
		this.engine = engine;
		engine.createGraph();
		universe = "Kevin Bacon";
	}
	
	/**
	 * Method reads each line, and based off of user's input, executes certain operations to carry them out
	 * @throws Exception
	 */
	public void readLine() throws Exception {
		Scanner in = new Scanner(System.in);
		String line;
		String[] pieces = "as ldkfj".split(" "); //initialize to something random
	
		Graph<String, Set<String>> shortestPath = GraphLib.bfs(engine.getGraph(), universe);
		System.out.println("\n" + universe + " is now the center of the acting universe, connected to "+ (engine.getGraph().numVertices()-GraphLib.missingVertices(engine.getGraph(), shortestPath).size())+"/" + engine.getActorList().size()+ " actors with average separation "+ GraphLib.averageSeparation(shortestPath, universe) + ".");
		System.out.println("\n" + universe +  " game > ");
		//Keep looking at user input as long as q is not the last command
		while (!pieces[0].equals("q")) {
			//Find out what the first input is in order to take action 
			line = in.nextLine();
			pieces = line.split(" ");
			//C command: sort by average path length, only considering those connected to universe
			if (pieces[0].equals("c")) {
				System.out.println("Enter low or high to sort accordingly");
				line = in.nextLine();
				
				Graph<String, Set<String>> shortestPathTree = GraphLib.bfs(engine.getGraph(), universe);
				Iterable<String> allActors = shortestPathTree.vertices();
				
				if (line.equals("low")) {
					System.out.println("List of centers of the universe, sorted from least average separation to greatest, is as follows: ");
					AvgPathComparator comparator = new AvgPathComparator(engine.getGraph());
					ArrayList<String> actorList = new ArrayList<String>();
					for (String actor: allActors) {
						actorList.add(actor);
					}
					actorList.sort(comparator);
					for (String actor: actorList) {
						System.out.println(actor);
					}
				}
				else if (line.equals("high")) {
					System.out.println("List of centers of the universe, sorted from greatest average separation to least, is as follows: ");
					AvgPathComparator2 comparator = new AvgPathComparator2(engine.getGraph());
					ArrayList<String> actorList = new ArrayList<String>();
					for (String actor: allActors) {
						actorList.add(actor);
					}
					actorList.sort(comparator);
					for (String actor: actorList) {
						System.out.println(actor);
					}
				}
				else {
					throw new Exception("The input after 'c' command must be either 'low' or 'high' (case-sensitive)");
				}
			}
			//Command d, return the list of ordered co-stars
			else if (pieces[0].equals("d")) {
				java.util.List<String> degreeList = GraphLib.verticesByInDegree(engine.getGraph());
				
				System.out.println("Enter either low or high to sort accordingly: ");
				line = in.nextLine();
				
				if (line.equals("low")) {
					System.out.println("The list of actors ordered from least to most co-stars is as follows: ");
					for (int j = degreeList.size()-1; j>=0; j--) {
						System.out.println(degreeList.get(j));
					}
				}
				else if (line.equals("high")) {
					System.out.println("The list of actors ordered from most to least co-stars is as follows: ");
					for (String actor: degreeList) {
						System.out.println(actor);
					}
				}
				else {
					throw new Exception("The input must be either 'low' or 'high.'");
				}
			}
			else if (pieces[0].equals("i")) {
				Graph<String, Set<String>> shortestPathTree = GraphLib.bfs(engine.getGraph(), universe);
				Set<String> missing = GraphLib.missingVertices(engine.getGraph(), shortestPathTree);
				System.out.println("Here are the actors with infinite separation from " +universe+ ": ");
				for (String actor: missing) {
					System.out.println(actor);
				}
			}
			else if (pieces[0].equals("p")) {
				Graph<String, Set<String>> shortestPathTree = GraphLib.bfs(engine.getGraph(), universe);
				
				System.out.println("Enter the name of the actor you want to find the path for");
				line = in.nextLine();
				//If it is an invalid actor name, throw an exception
				if(!engine.getGraph().hasVertex(line)) {
					throw new Exception("Actor name is invalid");
				}
				//Check to see if input contains any actors that are not connected to the universe at all
				if (GraphLib.missingVertices(engine.getGraph(), shortestPathTree).contains(line)) {
					System.out.println(line +"'s number is infinity");
					System.out.println(line + " does not have a path");	
				}
				//Find regular path if there are no tricky exceptions
				else {
					java.util.List<String> pathList = GraphLib.getPath(shortestPathTree, line);
					//Print out the number for that actor
					System.out.println(line +"'s number is " + (pathList.size()-1));
					for (int j = 0; j<pathList.size(); j++) {
						if (j!=pathList.size()-1) {
							System.out.println(pathList.get(j) + " co-starred with " + pathList.get(j+1)+ " in "+shortestPathTree.getLabel(pathList.get(j), pathList.get(j+1)));
						}
					}
				}
			}
			
			else if (pieces[0].equals("s")) {
				System.out.println("Enter low or high to sort actors accordingly:");
				line = in.nextLine();
				
				Graph<String, Set<String>> shortestPathTree = GraphLib.bfs(engine.getGraph(), universe);
				Queue<String> actorsQueue = new LinkedList<String>();
				ArrayList<String> allConnected = new ArrayList<String>();
				
				String currentVertex = universe;
				actorsQueue.add(currentVertex);
				
				while (!actorsQueue.isEmpty()) {
					currentVertex = actorsQueue.remove();
					if (!currentVertex.equals(universe)) {
						allConnected.add(currentVertex);
					}
					Iterable<String> inNeighbors = shortestPathTree.inNeighbors(currentVertex);
					for (String neighbor: inNeighbors) {
						actorsQueue.add(neighbor);	
					}
				}
				if (line.equals("low")) {
					System.out.println("The following is all actors who are connected somehow to " +universe+", ordered from least to greatest in separation: ");
					for (String actor: allConnected) {
						System.out.println(actor);
					}
				}
				else if (line.equals("high")) {
					System.out.println("The following is all actors who are connected somehow to " +universe+", ordered from greatest to least in separation: ");
					for (int j = allConnected.size()-1; j >= 0; j--) {
						System.out.println(allConnected.get(j));
					}
				}
				else {
					throw new Exception("You must input either 'low' or 'high'");
				}
			}
			else if (pieces[0].equals("u")) {
				System.out.println("Enter actor name to make as new head of universe:");
				line = in.nextLine();
				if (!engine.getGraph().hasVertex(line)) {
					throw new Exception("Invalid actor name");
				}
				universe = line;
				Graph<String, Set<String>> shortestPathTree = GraphLib.bfs(engine.getGraph(), universe);
				System.out.println("\n" + universe + " is now the center of the acting universe, connected to "+ (engine.getGraph().numVertices()-GraphLib.missingVertices(engine.getGraph(), shortestPathTree).size())+"/" + engine.getActorList().size()+ " actors with average separation "+ GraphLib.averageSeparation(shortestPathTree, universe) + ".");
				System.out.println("\n" + universe +  " game > ");
				
			}
			else if (pieces[0].equals("q")) {
				System.out.println("\n You have quit the game. To play again, simply re-run the program!");
			}
			else {
				throw new Exception("Invalid input");
			}
		}
	}

/**
 * Method sets up the game command interface.
 */
	public void gameSetUp() {
		System.out.println("Commands:");
		System.out.println("\t c <#>: list top (positive number) or bottom (negative) <#> centers of the universe, sorted by average separation");
		System.out.println("\t d <low> <high>: list actors sorted by degree, with degree between low and high");
		System.out.println("\t i: list actors with infinite separation from the current center");
		System.out.println("\t p <name>: find path from <name> to current center of the universe");
		System.out.println("\t s <low> <high>: list actors sorted by non-infinite separation from the current center, with separation between low and high");
		System.out.println("\t u <name>: make <name> the center of the universe");
		System.out.println("\t q: quit game");	
	}
	
	public static void main(String[]args)  {
		InputProcessor engine = new InputProcessor("actors.txt", "movies.txt", "movie-actors.txt");
		GameInterface test = new GameInterface(engine);
		test.gameSetUp();
		try {
			test.readLine();
		} 
		catch (Exception e) {
			System.out.println("Error found " + e);
		}
	}
}
