package com.github.quadinsa5if.findingandqueryingtext.model.vocabulary;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

import java.util.List;

public interface Vocabulary {

  /**
   * Return a posting list for a given term
   * @param term The term to query
   * @return The posting list for the term (empty array if no posting list was found)
   */
  List<Entry> getPostingList(String term);

  /**
   * Return the list of terms that belongs to the vocabulary
   * @return The list of terms of the vocabulary
   */
  List<String> getTerms();


}
