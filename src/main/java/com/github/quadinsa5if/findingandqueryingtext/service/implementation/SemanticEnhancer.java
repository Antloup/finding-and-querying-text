package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.CosinusSimilarity;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.VectorDistance;

import java.util.*;

public class SemanticEnhancer {

    private Map<String, int[]> contextVectors;

    public SemanticEnhancer() {

    }

    public String[] enhanceTerms(String[] terms, int k) {

        Set<String> termsSet = new HashSet();

        for(String term : terms) {
            termsSet.add(term);

            for(String enhancedTerm : this.enhanceTerm(term, k, new CosinusSimilarity())) {
                termsSet.add(enhancedTerm);
            }
        }

        String[] result = new String[termsSet.size()];

        int i = 0;
        for(String t : termsSet) {
            result[i++] = t;
        }

        return result;
    }

    public void loadContextVectors(Map<String, int[]> contextVectors) {
        this.contextVectors = contextVectors;
    }

    private String[] enhanceTerm(String term, int k, VectorDistance vectorDistance) {

        if(this.contextVectors.containsKey(term)) {

            int[] contextVectorReference = this.contextVectors.get(term);

            PriorityQueue<String> sortedQueue =
                    new PriorityQueue<String>(10, new Comparator<String>() {
                        @Override
                        public int compare(String t1, String t2) {
                            double dist1 = vectorDistance.compute(contextVectors.get(t1), contextVectorReference);
                            double dist2 = vectorDistance.compute(contextVectors.get(t2), contextVectorReference);
                            return vectorDistance.isBetter(dist1, dist2) ? 1 : -1;
                        }
                    });

            for(String t : this.contextVectors.keySet()) {
                sortedQueue.add(t);

                if(sortedQueue.size() > k) {
                    sortedQueue.poll();
                }
            }

            String[] result = new String[sortedQueue.size()];

            int i = 0;
            for(String t : sortedQueue) {
                result[i++] = t;
            }

            return result;

        } else {
            return new String[0];
        }

    }
}