package net.coderodde.graph.pathfinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.coderodde.graph.DirectedGraph;
import net.coderodde.graph.DirectedGraphWeightFunction;

/**
 * This abstract class defines some facilities shared by pathfinding algorithms
 * and API for using them.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Oct 6, 2016)
 */
public abstract class AbstractPathfinder {

    public static final class HeapEntry implements Comparable<HeapEntry> {

        private final Integer nodeId;
        private final double distance; // The priority key.

        public HeapEntry(Integer nodeId, double distance) {
            this.nodeId = nodeId;
            this.distance = distance;
        }

        public int getNode() {
            return nodeId;
        }

        public double getDistance() {
            return distance;
        }

        @Override
        public int compareTo(HeapEntry o) {
            return Double.compare(distance, o.distance);
        }
    }

    /**
     * The graph to search in.
     */
    protected final DirectedGraph graph;

    /**
     * The weight function to use.
     */
    protected final DirectedGraphWeightFunction weightFunction;

    protected AbstractPathfinder(DirectedGraph graph,
                                 DirectedGraphWeightFunction weightFunction) {
        this.graph = Objects.requireNonNull(graph, "The input graph is null.");
        this.weightFunction =
                Objects.requireNonNull(weightFunction,
                                       "The input weight function is null.");
    }

    protected AbstractPathfinder() {
        this.graph = null;
        this.weightFunction = null; // Compiler requires this initialization.
    }

    /**
     * Searches and returns a shortest path starting from the node 
     * {@code sourceNodeId} and leading to {@code targetNodeId}.
     * 
     * @param sourceNodeId the source node.
     * @param targetNodeId the target node.
     * @return a shortest path of nodes from source node to target node
     *         (including the terminal nodes) or an empty list if target is not
     *         reachable from source.
     */
    public abstract List<Integer> search(int sourceNodeId, int targetNodeId);

    /**
     * Reconstructs a shortest path from the data structures maintained by a 
     * <b>bidirectional</b> pathfinding algorithm.
     * 
     * @param touchNodeId the node where the two search frontiers agree.
     * @param PARENTSA the parent map in the forward search direction.
     * @param PARENTSB the parent map in the backward search direction.
     * @return the shortest path.
     */
    protected List<Integer> tracebackPath(int touchNodeId, 
                                          Map<Integer, Integer> PARENTSA,
                                          Map<Integer, Integer> PARENTSB) {
        List<Integer> path = new ArrayList<>();
        Integer currentNodeId = touchNodeId;

        while (currentNodeId != null) {
            path.add(currentNodeId);
            currentNodeId = PARENTSA.get(currentNodeId);
        }

        Collections.<Integer>reverse(path);

        if (PARENTSB != null) {
            currentNodeId = PARENTSB.get(touchNodeId);

            while (currentNodeId != null) {
                path.add(currentNodeId);
                currentNodeId = PARENTSB.get(currentNodeId);
            }
        }

        return path;
    }

    /**
     * Reconstructs a shortest path from the data structures maintained by a
     * unidirectional pathfinding algorithm.
     * 
     * @param targetNodeId the target node.
     * @param PARENTS      the parents map.
     * @return the shortest path.
     */
    protected List<Integer> tracebackPath(int targetNodeId, 
                                          Map<Integer, Integer> PARENTS) {
        return tracebackPath(targetNodeId, PARENTS, null);
    }
}