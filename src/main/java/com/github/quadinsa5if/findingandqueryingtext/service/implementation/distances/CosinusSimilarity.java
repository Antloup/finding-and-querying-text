package com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances;

public class CosinusSimilarity implements VectorDistance {
    @Override
    public double compute(int[] vectorA, int[] vectorB) {
        int dotProduct = 0;
        int normA = 0;
        int normB = 0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += vectorA[i] * vectorA[i];
            normB += vectorB[i] * vectorB[i];
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    @Override
    public boolean isBetter(double similarityA, double similarityB) {
        return similarityA > similarityB;
    }
}
