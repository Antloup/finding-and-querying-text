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

public class NativeSolverImpl implements QuerySolver {

  private final Vocabulary vocabulary;

  public NativeSolverImpl(Vocabulary vocabulary) {
    this.vocabulary = vocabulary;
  }

  @Override
  public Iter<Integer> answer(String[] terms, int k) {

    final List<Entry> postingListForTerms = new ArrayList<>();
    for (String term : terms) {
      postingListForTerms.addAll(vocabulary.getPostingList(term));
    }

    final Map<Integer, Double> mergedScores = postingListForTerms
        .stream()
        .collect(Collectors.groupingBy(Entry::articleId, averagingDouble(Entry::score)));

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
