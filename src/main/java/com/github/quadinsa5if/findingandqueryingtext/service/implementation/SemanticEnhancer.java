package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.CosinusSimilarity;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.EuclideanDistance;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.ManatthanDistance;
import com.github.quadinsa5if.findingandqueryingtext.service.implementation.distances.VectorDistance;

import java.io.*;
import java.util.*;

public class SemanticEnhancer {

    private Map<String, int[]> contextVectors;

    public SemanticEnhancer() {

    }

    public String[] enhanceTerms(String[] terms, int k) {

        Set<String> termsSet = new HashSet();

        for (String term : terms) {
            termsSet.add(term);

            for (String enhancedTerm : this.enhanceTerm(term, k, new ManatthanDistance())) {
                termsSet.add(enhancedTerm);
            }
        }

        String[] result = new String[termsSet.size()];

        int i = 0;
        for (String t : termsSet) {
            result[i++] = t;
        }

        return result;
    }

    private String[] enhanceTerm(String term, int k, VectorDistance vectorDistance) {

        if (this.contextVectors.containsKey(term)) {

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

            for (String t : this.contextVectors.keySet()) {
                sortedQueue.add(t);

                if (sortedQueue.size() > k) {
                    sortedQueue.poll();
                }
            }

            String[] result = new String[sortedQueue.size()];

            int i = 0;
            for (String t : sortedQueue) {
                result[i++] = t;
            }

            return result;

        } else {
            return new String[0];
        }

    }

    public void loadContextVectorsFromFile(String filename) {

        FileInputStream fis = null;
        try {
            fis = new FileInputStream(filename);

        ObjectInputStream ois = new ObjectInputStream(fis);
        this.contextVectors = (HashMap) ois.readObject();
        ois.close();
        fis.close();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading context vectors from file");
            e.printStackTrace();
        }
    }

    public void loadAndSaveContextVectorsToFile(Map<String, int[]> contextVectors, String filename) {

        this.contextVectors = contextVectors;

        try {
            FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.contextVectors);
            oos.close();
            fos.close();
        } catch (IOException e) {
            System.err.println("Error saving context vectors in file");
            e.printStackTrace();
        }
    }
}