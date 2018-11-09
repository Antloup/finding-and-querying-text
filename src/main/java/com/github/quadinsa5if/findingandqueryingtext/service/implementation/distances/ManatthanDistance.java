package com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances;

public class ManatthanDistance implements VectorDistance {
    @Override
    public double compute(int[] vectorA, int[] vectorB) {
        int distance = 0;

        for(int i = 0; i < vectorA.length && i < vectorB.length; i++) {
            distance += (vectorA[i] - vectorB[i]);
        }

        return distance;
    }

    @Override
    public boolean isBetter(double distanceA, double distanceB) {
        return distanceA < distanceB;
    }
}
