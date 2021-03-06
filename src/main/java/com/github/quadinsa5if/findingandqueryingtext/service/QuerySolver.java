package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.Vocabulary;

public interface QuerySolver {

  /**
   * Return the top k articles where the terms have the better score
   * @param terms The list of term to search
   * @param k The number of articles to return (must be >= 0)
   * @return An iterator other the top-k articles
   */
  Iter<Integer> answer(Vocabulary vocabulary, String[] terms, int k);

}
