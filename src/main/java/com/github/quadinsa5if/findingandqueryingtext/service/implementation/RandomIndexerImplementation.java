package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.service.DatasetVisitor;

import java.io.File;
import java.util.*;

public class RandomIndexerImplementation implements DatasetVisitor {


    private static final int VECTOR_SIZE = 100;
    // Important: Must be even
    private static final int NON_ZERO_ELEMENTS = 4;


    private Map<Integer, int[]> indexVectors;
    private Map<String, int[]> contextVectors;
    private List<Integer> nonZeroIndexes;
    private int currentArticleId;

    public RandomIndexerImplementation() {
        indexVectors = new HashMap<>();
        contextVectors = new HashMap<>();

        nonZeroIndexes = new ArrayList<>(VECTOR_SIZE);
        for (int i = 0; i < VECTOR_SIZE; i++) {
            nonZeroIndexes.add(i);
        }
    }

    private int[] createRandomVector() {
        int[] vector = createZeroVector();
        Collections.shuffle(nonZeroIndexes);
        for (int i = 0; i < NON_ZERO_ELEMENTS; i++) {
            int k = nonZeroIndexes.get(i);
            vector[k] = (i % 2 == 0) ? 1 : -1;
        }
        return vector;
    }

    private int[] createZeroVector() {
        return new int[VECTOR_SIZE];
    }

    private void addTo(int[] target, int[] other) {
        assert target.length == other.length;
        for (int i = 0; i < target.length; i++) {
            target[i] += other[i];
        }
    }

    @Override
    public int getTotalPassNumber() {
        return 1;
    }

    @Override
    public void onOpeningArticle(int articleId, int currentPassNumber) {
        currentArticleId = articleId;
        if (!indexVectors.containsKey(articleId)) {
            indexVectors.put(articleId, createRandomVector());
        }
    }

    @Override
    public void onTermRead(String term, int currentPassNumber) {
        contextVectors.putIfAbsent(term, createZeroVector());
        addTo(contextVectors.get(term), indexVectors.get(currentArticleId));
    }

    public Map<String, int[]> getContextVectors() {
        return contextVectors;
    }

    @Override
    public void onClosingArticle(int articleId, int currentPassNumber) {
    }

    @Override
    public void onEndingPass(int currentPassNumber) {
    }

    @Override
    public void onOpeningFile(File file, int currentPassNumber) {
    }
}
