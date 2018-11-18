package com.github.quadinsa5if.findingandqueryingtext.service.implementation;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;
import com.github.quadinsa5if.findingandqueryingtext.service.QuerySolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.averagingDouble;
import static java.util.stream.Collectors.summingDouble;

public class NaiveSolverImpl implements QuerySolver {

  public NaiveSolverImpl() {}

  @Override
  public Iter<Integer> answer(Vocabulary vocabulary, String[] terms, int k) {

    final List<Entry> postingListForTerms = new ArrayList<>();
    for (String term : terms) {
      postingListForTerms.addAll(vocabulary.getPostingList(term));
    }

    final Map<Integer, Double> mergedScores = postingListForTerms
        .stream()
        .collect(Collectors.groupingBy(Entry::articleId, summingDouble(Entry::score)));

    final List<Integer> bestKScores = mergedScores.entrySet()
        .stream()
        .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
        .limit((long) k)
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());

    return new Iter<Integer>() {
      int i = 0;
      @Override
      public Optional<Integer> next() {
        Optional<Integer> result;
        if (i < bestKScores.size()) {
          result = Optional.of(bestKScores.get(i));
          i += 1;
        } else {
          result = Optional.empty();
        }
        return result;
      }
    };

  }
}
