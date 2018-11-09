package com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances;

public class EuclideanDistance implements VectorDistance {

    public double compute(int[] vectorA, int[] vectorB) {

        int underSqrt = 0;

        for(int i = 0; i < vectorA.length && i < vectorB.length; i++) {
            underSqrt += (vectorA[i] - vectorB[i]) * (vectorA[i] - vectorB[i]);
        }

        return Math.sqrt(underSqrt);

    }

    @Override
    public boolean isBetter(double distanceA, double distanceB) {
        return distanceA < distanceB;
    }
}
