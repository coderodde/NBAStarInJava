import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
//import javafx.application.Application;
//import javafx.stage.Stage;
import net.coderodde.graph.DirectedGraph;
import net.coderodde.graph.DirectedGraphWeightFunction;
import net.coderodde.graph.pathfinding.AbstractPathfinder;
import net.coderodde.graph.pathfinding.DirectedGraphNodeCoordinates;
import net.coderodde.graph.pathfinding.HeuristicFunction;
import net.coderodde.graph.pathfinding.support.AStarPathfinder;
import net.coderodde.graph.pathfinding.support.BidirectionalDijkstraPathfinder;
import net.coderodde.graph.pathfinding.support.DijkstraPathfinder;
import net.coderodde.graph.pathfinding.support.EuclideanHeuristicFunction;
import net.coderodde.graph.pathfinding.support.NBAStarPathfinder;
import net.coderodde.graph.pathfinding.support.RandomThunderboltPathfinder;

public final class Demo /*extends Application*/ {

    private static final int NODES = 10000;
    private static final int ARCS  = 35000;
    
    private static final int GRAPHIC_DEMO_NODES = 10000;
    private static final int GRAPHIC_DEMO_ARCS  = 50000;
    
    public static void main(String[] args) {
        commandLineDemo();
    }

    public static void commandLineDemo() {
        long seed = System.nanoTime();
        Random random = new Random(seed);
        System.out.println("Seed = " + seed);

        long start = System.currentTimeMillis();
        DirectedGraph graph = getRandomGraph(NODES, ARCS, random);
        DirectedGraphNodeCoordinates coordinates = getCoordinates(graph, 
                                                                  random);
        DirectedGraphWeightFunction weightFunction = 
                getWeightFunction(graph, coordinates);

        List<Integer> graphNodeList = new ArrayList<>(graph.getNodeList());

        Integer sourceNodeId = choose(graphNodeList, random);
        Integer targetNodeId = choose(graphNodeList, random);
        long end = System.currentTimeMillis();

        System.out.println("Created the graph data structures in " +
                           (end - start) + " milliseconds.");

        System.out.println("Source: " + sourceNodeId);
        System.out.println("Target: " + targetNodeId);

        System.out.println();

        HeuristicFunction hf = new EuclideanHeuristicFunction(coordinates);

        AbstractPathfinder finder1 = new AStarPathfinder(graph,
                                                         weightFunction,
                                                         hf);

        AbstractPathfinder finder2 = new DijkstraPathfinder(graph,
                                                            weightFunction);

        AbstractPathfinder finder3 = 
                new BidirectionalDijkstraPathfinder(graph, weightFunction);
        
        AbstractPathfinder finder4 = new NBAStarPathfinder(graph, 
                                                           weightFunction,
                                                           hf);
        
        AbstractPathfinder finder5 = new RandomThunderboltPathfinder(graph, hf);
        
        start = System.currentTimeMillis();
        List<Integer> path1 = finder1.search(sourceNodeId, targetNodeId);
        end = System.currentTimeMillis();

        System.out.println("A* in " + (end - start) + " milliseconds.");

        path1.forEach(System.out::println);
        System.out.println();

        start = System.currentTimeMillis();
        List<Integer> path2 = finder2.search(sourceNodeId, targetNodeId);
        end = System.currentTimeMillis();

        System.out.println("Dijkstra in " + (end - start) + " milliseconds.");
        path2.forEach(System.out::println);
        System.out.println();

        start = System.currentTimeMillis();
        List<Integer> path3 = finder3.search(sourceNodeId, targetNodeId);
        end = System.currentTimeMillis();

        System.out.println("Bidirectional Dijkstra in " 
                + (end - start)
                + " milliseconds.");
        
        path3.forEach(System.out::println);
        System.out.println();
        
        start = System.currentTimeMillis();
        List<Integer> path4 = finder4.search(sourceNodeId, targetNodeId);
        end = System.currentTimeMillis();

        System.out.println("NBA* in " 
                + (end - start)
                + " milliseconds.");
        
        start = System.currentTimeMillis();
        List<Integer> path5 = finder5.search(sourceNodeId, targetNodeId);
        end = System.currentTimeMillis();

        System.out.println("Random Thunderbolt in " 
                + (end - start)
                + " milliseconds.");
        
        if (path5.isEmpty()) {
            System.out.println("Thunderbolt did not find a path.");
        } else {
            path5.forEach(System.out::println);
            System.out.println();
        }
        
        boolean algorithmsAgree =
                path1.equals(path2) 
             && path1.equals(path3)
             && path1.equals(path4);

        System.out.println("Algorithms agree: " + algorithmsAgree);
        
        if (!algorithmsAgree) {
            System.out.println("Exiting...");
            return;
        }
        
        double optimalPathLength = getPathLength(path1, weightFunction);
        
        System.out.println("Optimal path length: " + optimalPathLength);
        
        double thunderboltPathLength = getPathLength(path5, weightFunction);
        
        System.out.println("Thunderbolt path length: " + thunderboltPathLength);
        
        double pathLengthRatio = thunderboltPathLength / optimalPathLength;
        
        System.out.println("Path length ratio: " + pathLengthRatio);
    }

    private static DirectedGraph getRandomGraph(int nodes, 
                                                int arcs, 
                                                Random random) {
        DirectedGraph graph = new DirectedGraph();

        for (int id = 0; id < nodes; ++id) {
            graph.addNode(id);
        }

        List<Integer> graphNodeList = new ArrayList<>(graph.getNodeList());

        while (arcs-- > 0) {
            Integer tailNodeId = choose(graphNodeList, random);
            Integer headNodeId = choose(graphNodeList, random);
            graph.addArc(tailNodeId, headNodeId);
        }

        return graph;
    }
    
    private static double 
        getPathLength(
                List<Integer> path, 
                DirectedGraphWeightFunction weightFunction) {
        double pathLength = 0.0;
        
        for (int i = 0; i < path.size() - 1; ++i) {
            pathLength += weightFunction.get(path.get(i), path.get(i + 1)); 
        }
        
        return pathLength;
    }

    private static DirectedGraphNodeCoordinates 
        getCoordinates(DirectedGraph graph, Random random) {
        DirectedGraphNodeCoordinates coordinates =
                new DirectedGraphNodeCoordinates();

        for (Integer nodeId : graph.getNodeList()) {
            coordinates.put(nodeId, randomPoint(1000.0, 1000.0, random));
        }

        return coordinates;
    }

    private static DirectedGraphWeightFunction 
        getWeightFunction(DirectedGraph graph,
                          DirectedGraphNodeCoordinates coordinates) {
        DirectedGraphWeightFunction weightFunction = 
                new DirectedGraphWeightFunction();

        for (Integer nodeId : graph.getNodeList()) {
            Point2D.Double p1 = coordinates.get(nodeId);

            for (Integer childNodeId : graph.getChildrenOf(nodeId)) {
                Point2D.Double p2 = coordinates.get(childNodeId);
                double distance = p1.distance(p2);
                weightFunction.put(nodeId, childNodeId, 1.1 * distance);
            }
        }

        return weightFunction;
    }

    private static Point2D.Double randomPoint(double width,
                                              double height,
                                              Random random) {
        return new Point2D.Double(width * random.nextDouble(),
                                  height * random.nextDouble());
    }

    private static <T> T choose(List<T> list, Random random) {
        return list.get(random.nextInt(list.size()));
    }
//    
//    @Override
//    public void start(Stage primaryStage) throws Exception {
//        
//    }
}
