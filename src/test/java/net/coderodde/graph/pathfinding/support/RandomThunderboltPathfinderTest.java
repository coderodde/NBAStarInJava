package net.coderodde.graph.pathfinding.support;

import java.awt.geom.Point2D;
import java.util.List;
import net.coderodde.graph.DirectedGraph;
import net.coderodde.graph.pathfinding.AbstractPathfinder;
import net.coderodde.graph.pathfinding.DirectedGraphNodeCoordinates;
import net.coderodde.graph.pathfinding.HeuristicFunction;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class RandomThunderboltPathfinderTest {
    
    private static final DirectedGraph graph1 = new DirectedGraph();
    private static final DirectedGraph graph2 = new DirectedGraph();
    private static final DirectedGraphNodeCoordinates coordinates = 
            new DirectedGraphNodeCoordinates();
    
    private static final HeuristicFunction heuristicFunction = 
            new EuclideanHeuristicFunction(coordinates);
    
    @BeforeClass
    public static void setUpClass() {
        // Build disconnected graph:
        graph1.addNode(0);
        graph1.addNode(1);
        graph1.addNode(2);
        graph1.addNode(3);
        graph1.addNode(4);
        
        graph1.addArc(0, 1);
        graph1.addArc(1, 2);
        graph1.addArc(1, 3);
        
        // Build connected graph:
        graph2.addNode(0);
        graph2.addNode(1);
        graph2.addNode(2);
        graph2.addNode(3);
        graph2.addNode(4);
       
        graph2.addArc(0, 1);
        graph2.addArc(1, 2);
        graph2.addArc(1, 3);
        graph2.addArc(2, 4);
        graph2.addArc(3, 4);
        
        // Set node coordinates:
        coordinates.put(0, new Point2D.Double(0, 0));
        coordinates.put(1, new Point2D.Double(1, 0));
        coordinates.put(2, new Point2D.Double(2, 1));
        coordinates.put(3, new Point2D.Double(2, -1));
        coordinates.put(4, new Point2D.Double(3, 0));
    }
    
    @Test
    public void testSearch() {
        AbstractPathfinder pathfinder =
                new RandomThunderboltPathfinder(
                        graph1, 
                        heuristicFunction);
        
        List<Integer> nullPath = pathfinder.search(0, 4);
        
        assertNull(nullPath);
        
        pathfinder = 
                new RandomThunderboltPathfinder(
                        graph2, 
                        heuristicFunction);
        
        List<Integer> path = pathfinder.search(0, 4);
        
        assertEquals(4, path.size());
        assertEquals(Integer.valueOf(0), path.get(0));
        assertEquals(Integer.valueOf(1), path.get(1));
        int secondLastNode = path.get(2);
        assertTrue(secondLastNode == 2 || secondLastNode == 3);
        assertEquals(Integer.valueOf(4), path.get(3));
    }
}
