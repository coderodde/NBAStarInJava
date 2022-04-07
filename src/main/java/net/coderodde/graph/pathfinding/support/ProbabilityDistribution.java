package net.coderodde.graph.pathfinding.support;
 
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
 
final class ProbabilityDistribution {
 
    private static final class Entry {
 
        private final Integer element;
        private double weight;
 
        Entry(Integer element, double weight) {
            this.element = element;
            this.weight = weight;
        }
 
        Integer getElement() {
            return element;
        }
 
        double getWeight() {
            return weight;
        }
 
        void setWeight(double weight) {
            this.weight = weight;
        }
    }
 
    private final List<Entry> storage = new ArrayList<>();
    private final Map<Integer, Entry> map = new HashMap<>();
    private Random random;
    private double totalWeight;
    
    ProbabilityDistribution() {
        this(new Random());
    }
 
    ProbabilityDistribution(Random random) {
        this.random = random;
    }
 
    boolean addElement(Integer element, double weight) {
        Entry entry = map.get(element);
 
        if (entry != null) {
            entry.setWeight(entry.getWeight() + weight);
        } else {
            entry = new Entry(element, weight);
            map.put(element, entry);
            storage.add(entry);
        }
 
        totalWeight += weight;
        return true;
    }
 
    Integer sampleElement() {
        double value = random.nextDouble() * totalWeight;
        int distributionSize = storage.size();
 
        for (int i = 0; i < distributionSize; ++i) {
            Entry entry = storage.get(i);
            double currentWeight = entry.getWeight();
 
            if (value < currentWeight) {
                return entry.getElement();
            }
 
            value -= currentWeight;
        }
        
        // Should not happen often:
        return storage.get(random.nextInt(storage.size())).element;
    }
    
    boolean isEmpty() {
        return storage.isEmpty();
    }
}