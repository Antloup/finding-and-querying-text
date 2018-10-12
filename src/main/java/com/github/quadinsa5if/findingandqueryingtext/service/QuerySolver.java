package com.github.quadinsa5if.findingandqueryingtext.service;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import com.github.quadinsa5if.findingandqueryingtext.model.ArticleId;

public interface QuerySolver {

  /**
   * Return the top k articles where the terms have the better score
   * @param terms The list of term to search
   * @param k The number of articles to return
   * @return An iterator other the articles
   */
  Iter<ArticleId> answer(String[] terms, int k);

}
