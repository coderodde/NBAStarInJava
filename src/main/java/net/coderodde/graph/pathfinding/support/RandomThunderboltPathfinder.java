package net.coderodde.graph.pathfinding.support;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
        Integer currentNodeId = sourceNodeId;
        
        Set<Integer> settledNodes = new HashSet<>();
        List<Integer> path = new ArrayList<>();
        
        path.add(sourceNodeId);
        settledNodes.add(sourceNodeId);
        
        while (!currentNodeId.equals(targetNodeId)) {
            Integer nextNodeId = 
                    step(currentNodeId,
                         targetNodeId,
                         settledNodes);
            
            if (nextNodeId == null) {
                currentNodeId = path.remove(path.size() - 1);
                
                if (currentNodeId.equals(sourceNodeId)) {
                    return null;
                }
                
                settledNodes.remove(currentNodeId);
                continue;
            }
            
            if (settledNodes.contains(nextNodeId)) {
                continue;
            }
            
            settledNodes.add(nextNodeId);
            path.add(nextNodeId);
            currentNodeId = nextNodeId;
        }
        
        return path;
    }
    
    private Integer 
        step(Integer currentNodeId, 
             Integer targetNodeId,
             Set<Integer> settledNodes) {
            
        Set<Integer> children = graph.getChildrenOf(currentNodeId);
        
        if (children.isEmpty()) {
            // Should not happen but is possible.
            return null;
        }
        
        return getMinimizingNode(children, settledNodes, targetNodeId);
    }
        
    private Integer 
        getMinimizingNode(
                Set<Integer> children,
                Set<Integer> settledNodes,
                Integer targetNodeId) {
        double minimizingCost = Double.POSITIVE_INFINITY;
        Integer minimizingNodeId = null;
        
        for (Integer childNodeId : children) {
            if (settledNodes.contains(childNodeId)) {
                continue;
            }
            
            double tentativeCost = 
                    heuristicFunction.estimateDistanceBetween(
                            childNodeId, 
                            targetNodeId);
            
            if (minimizingCost > tentativeCost) {
                minimizingCost = tentativeCost;
                minimizingNodeId = childNodeId;
            }
        }
        
        return minimizingNodeId;
    }
}
