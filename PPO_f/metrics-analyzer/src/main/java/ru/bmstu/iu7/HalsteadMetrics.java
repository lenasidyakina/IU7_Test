package ru.bmstu.iu7;

import java.util.Set;

public class HalsteadMetrics {

    private final Set<String> uniqueOperators;
    private final Set<String> uniqueOperands;
    private final int totalOperators;
    private final int totalOperands;

    public HalsteadMetrics(Set<String> uniqueOperators, Set<String> uniqueOperands,
                           int totalOperators, int totalOperands) {
        this.uniqueOperators = uniqueOperators;
        this.uniqueOperands = uniqueOperands;
        this.totalOperators = totalOperators;
        this.totalOperands = totalOperands;
    }

    public double getVolume() {
        int n1 = uniqueOperators.size();
        int n2 = uniqueOperands.size();
        int N = totalOperators + totalOperands;
        int n = n1 + n2;
        return N * (Math.log(n) / Math.log(2));
    }

    public double getDifficulty() {
        int n1 = uniqueOperators.size();
        int n2 = uniqueOperands.size();
        return (n2 == 0) ? 0 : (n1 / 2.0) * (totalOperands / (double) n2);
    }

    public double getEffort() {
        return getVolume() * getDifficulty();
    }

    public Set<String> getUniqueOperators() { return uniqueOperators; }
    public Set<String> getUniqueOperands() { return uniqueOperands; }
    public int getTotalOperators() { return totalOperators; }
    public int getTotalOperands() { return totalOperands; }
}
