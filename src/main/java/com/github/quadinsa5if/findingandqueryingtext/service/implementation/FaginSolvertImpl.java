package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;

import java.util.*;

public class FaginSolvertImpl implements QuerySolver {

    private final Vocabulary vocabulary;

    public FaginSolvertImpl(Vocabulary vocabulary) {
        this.vocabulary = vocabulary;
    }

    @Override
    public Iter<Integer> answer(String[] terms, int k) {
        return null;
    }

    public void topKQuery(String[] terms, int k) {

        final Map<String, List<Entry>> scoreSortEntries = new HashMap<>();
        final Map<String, Map<Integer, Entry>> randomAccessEntries = new HashMap<>();
        for (String term : terms) {
            final List<Entry> sortedEntriesForTerm = Optional.ofNullable(vocabulary.getPostingList(term))
                    .orElse(new ArrayList<>());
            final Map<Integer, Entry> randomAccessEntriesForTerm = Optional.ofNullable(randomAccessEntries.get(term))
                    .orElse(new HashMap<>());

            scoreSortEntries.put(term, sortedEntriesForTerm);
            for (Entry entry : sortedEntriesForTerm) {
                randomAccessEntriesForTerm.put(entry.articleId, entry);
            }
            randomAccessEntries.put(term, randomAccessEntriesForTerm);
        }

        final List<Entry> M = new ArrayList<>();
        final List<Entry> C = new ArrayList<>();

        while (C.size() != k) {

        }

    }

    public void sortedAccess(Map<String, List<Entry>> scoreSortEntries) {


    }

}
