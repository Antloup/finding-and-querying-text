package com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances;

import java.util.Vector;

public interface VectorDistance {

    double compute(int[] vectorA, int[] vectorB);

    boolean isBetter(double measureA, double measureB);
}
