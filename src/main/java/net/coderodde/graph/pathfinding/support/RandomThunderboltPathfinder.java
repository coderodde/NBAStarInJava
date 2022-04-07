package net.coderodde.graph.pathfinding.support;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.coderodde.graph.DirectedGraph;
import net.coderodde.graph.pathfinding.AbstractPathfinder;
import net.coderodde.graph.pathfinding.HeuristicFunction;

/**
 * This class implements the random "Thunderbolt" pathfinder.
 * 
 * @author Rodion "rodde" Efremov
 * @version 1.6 (Apr 7, 2022)
 * @since 1.6 (Apr 7, 2022)
 */
public final class RandomThunderboltPathfinder extends AbstractPathfinder {

    private static final double MAXIMUM_HEURISTIC_ESTIMATE_FACTOR = 1.15;
    
    private final HeuristicFunction heuristicFunction;
    private final DirectedGraph graph;
    
    public RandomThunderboltPathfinder(DirectedGraph graph,
                                       HeuristicFunction heuristicFunction) {
        this.graph = Objects.requireNonNull(graph, "The input graph is null.");
        this.heuristicFunction = 
                Objects.requireNonNull(
                        heuristicFunction, 
                        "The input heuristic function is null.");
    }
    
    @Override
    public List<Integer> search(int sourceNodeId, int targetNodeId) {
        Map<Integer, Integer> parentMap = new HashMap<>();
        Random random = new Random();
        Integer currentNodeId = sourceNodeId;
        List<Integer> path = new ArrayList<>();
        
        while (!currentNodeId.equals(targetNodeId)) {
            currentNodeId = 
                    step(currentNodeId,
                         targetNodeId,
                         random);
            
            if (currentNodeId != null) {
                path.add(currentNodeId);
            } else {
                if (path.isEmpty()) {
                    return null;
                }
                
                currentNodeId = path.remove(path.size() - 1);
            }
        }
        
        return tracebackPath(targetNodeId, parentMap);
    }
    
    private Integer 
        step(Integer currentNodeId, 
             Integer targetNodeId, 
             Random random) {
            
        Set<Integer> children = graph.getChildrenOf(currentNodeId);
        
        if (children.isEmpty()) {
            // Should not happen but is possible.
            return null;
        }
        
        double maximumHeuristicEstimate = 
                computeMaximumHeuristicEstimate(children, targetNodeId);
        
        maximumHeuristicEstimate *= MAXIMUM_HEURISTIC_ESTIMATE_FACTOR;
        
        ProbabilityDistribution distribution =
                new ProbabilityDistribution(random);
        
        for (Integer childNodeId : children) {
            double currentHeuristicEstimate = 
                    heuristicFunction
                            .estimateDistanceBetween(
                                    childNodeId, 
                                    targetNodeId);
            
            double distributionWeight = 
                    maximumHeuristicEstimate - currentHeuristicEstimate;
            
            distribution.addElement(childNodeId, distributionWeight);
        }
        
        return distribution.sampleElement();
    }
    
    private double
        computeMaximumHeuristicEstimate(
                Set<Integer> children, 
                Integer targetNodeId) {
            
        double maximumHeuristicEstimate = Double.NEGATIVE_INFINITY;
        
        for (Integer childNodeId : children) {
            double tentativeHeuristicEstimate = 
                    heuristicFunction.estimateDistanceBetween(
                            childNodeId, 
                            targetNodeId);
            
            maximumHeuristicEstimate = 
                    Math.max(maximumHeuristicEstimate, 
                             tentativeHeuristicEstimate);
        }
        
        return maximumHeuristicEstimate;
    }
}
