package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;

import java.io.File;
import java.util.*;

public class RandomIndexerImplementation implements DatasetVisitor {

    private HashMap<Integer, Vector<Integer>> documentVectors;
    private HashMap<String, Vector<Integer>> termVectors;

    private List<Integer> nonZeroIndexes;

    private final int VECTOR_SIZE = 100;
    private final int NON_ZERO_ELEMENTS = 20;


    private int currentArticleId;

    public RandomIndexerImplementation() {
        documentVectors = new HashMap<>();
        termVectors = new HashMap<>();

        nonZeroIndexes = new ArrayList<>();

        for(int i = 0; i < VECTOR_SIZE; i++) {
            nonZeroIndexes.add(i);
        }
    }

    private Vector<Integer> createRandomVector() {

        Vector<Integer> vector = createZeroVector();

        Collections.shuffle(nonZeroIndexes);

        for(int i = 0; i < NON_ZERO_ELEMENTS; i++) {
            int k = nonZeroIndexes.get(i);
            vector.setElementAt((i%2 == 0) ? 1 : -1, k);
        }

        return vector;
    }

    private Vector<Integer> createZeroVector() {

        Vector<Integer> vector = new Vector(VECTOR_SIZE);

        for(int i = 0; i < VECTOR_SIZE; i++) {
            vector.addElement(0);
        }

        return vector;
    }

    private void addVector(Vector<Integer> target, Vector<Integer> with) {

        for(int i = 0; i < VECTOR_SIZE; i++) {
            int newValue = target.get(i) + with.get(i);
            target.setElementAt(newValue, i);
        }
    }

    @Override
    public int getTotalPassNumber() {
        return 1;
    }

    @Override
    public void onArticleParseStart(int articleId, int currentPassNumber) {
        documentVectors.put(articleId, createRandomVector());

        currentArticleId = articleId;
    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {

        if(!termVectors.containsKey(term)) {
            termVectors.put(term, createZeroVector());
        }

        addVector(termVectors.get(term), documentVectors.get(currentArticleId));

    }

    public HashMap<String, Vector<Integer>> getTermVectors() {
        return termVectors;
    }

    @Override
    public void onArticleParseEnd(int articleId, int currentPassNumber) {

    }

    @Override
    public void onPassEnd(int currentPassNumber) {

    }

    @Override
    public void onPassStart(File file, int currentPassNumber) {

    }
}
