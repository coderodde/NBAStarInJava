package net.coderodde.graph.pathfinding.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.coderodde.graph.DirectedGraph;
import net.coderodde.graph.DirectedGraphWeightFunction;
import net.coderodde.graph.pathfinding.AbstractPathfinder;
import net.coderodde.graph.pathfinding.AbstractPathfinder.HeapEntry;

public class BidirectionalDijkstraPathfinder extends AbstractPathfinder {
    
    
    public BidirectionalDijkstraPathfinder(DirectedGraph graph, 
                                           DirectedGraphWeightFunction weightFunction) {
        super(graph, weightFunction);
    }

    @Override
    public List<Integer> search(int sourceNodeId, int targetNodeId) {
        Queue<HeapEntry> openForward  = new PriorityQueue<>();
        Queue<HeapEntry> openBackward = new PriorityQueue<>();
        Set<Integer> closedForward   = new HashSet<>();
        Set<Integer> closedBackward  = new HashSet<>();
        Map<Integer, Double> distanceForward  = new HashMap<>();
        Map<Integer, Double> distanceBackward = new HashMap<>();
        Map<Integer, Integer> parentForward  = new HashMap<>();
        Map<Integer, Integer> parentBackward = new HashMap<>(); 
        
        // Initializing state:
        double bestPathLength = Double.MAX_VALUE;
        Integer touchNode = null;
        
        openForward .add(new HeapEntry(sourceNodeId, 0.0));
        openBackward.add(new HeapEntry(targetNodeId, 0.0));
        
        distanceForward .put(sourceNodeId, 0.0);
        distanceBackward.put(targetNodeId, 0.0);
        
        parentForward .put(sourceNodeId, null);
        parentBackward.put(targetNodeId, null);
        
        while (!openForward.isEmpty() && !openBackward.isEmpty()) {
            double temporaryPathLength =
                    distanceForward .get(openForward .peek().getNode()) + 
                    distanceBackward.get(openBackward.peek().getNode());
            
            if (temporaryPathLength > bestPathLength) {
                return tracebackPath(touchNode, parentForward, parentBackward);
            }
            
            if (openForward.size() + closedForward.size() < 
                openBackward.size() + closedBackward.size()) {
                Integer currentNode = openForward.remove().getNode();
                closedForward.add(currentNode);
                
                for (Integer childNode : graph.getChildrenOf(currentNode)) {
                    if (closedForward.contains(childNode)) {
                        continue;
                    }
                    
                    double tentativeScore = 
                            distanceForward.get(currentNode) +
                            weightFunction.get(currentNode, childNode);
                    
                    if (!distanceForward.containsKey(childNode) ||
                             distanceForward.get(childNode) > tentativeScore) {
                        distanceForward.put(childNode, tentativeScore);
                        parentForward.put(childNode, currentNode);
                        openForward.add(
                                new HeapEntry(childNode, 
                                              tentativeScore));
                        
                        if (closedBackward.contains(childNode)) {
                            double pathLength = 
                                    tentativeScore +
                                    distanceBackward.get(childNode);
                            
                            if (bestPathLength > pathLength) {
                                bestPathLength = pathLength;
                                touchNode = childNode;
                            }
                        }
                    }
                }
            } else {
                Integer currentNode = openBackward.remove().getNode();
                closedBackward.add(currentNode);
                
                for (Integer parentNode : graph.getParentsOf(currentNode)) {
                    if (closedBackward.contains(parentNode)) {
                        continue;
                    }

                    double tentativeScore = 
                            distanceBackward.get(currentNode) +
                            weightFunction.get(parentNode, currentNode);

                    if (!distanceBackward.containsKey(parentNode) ||
                             distanceBackward.get(parentNode) > tentativeScore) {
                        distanceBackward.put(parentNode, tentativeScore);
                        parentBackward.put(parentNode, currentNode);
                        openBackward.add(
                                new HeapEntry(parentNode, 
                                              tentativeScore));

                        if (closedForward.contains(parentNode)) {
                            double pathLength = 
                                    tentativeScore +
                                    distanceForward.get(parentNode);

                            if (bestPathLength > pathLength) {
                                bestPathLength = pathLength;
                                touchNode = parentNode;
                            }
                        }
                    }
                }   
            }
        }
        
        return Collections.<Integer>emptyList();
    }
}
