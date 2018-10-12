package com.github.quadinsa5if.findingandqueryingtext.model.vocabulary;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;

public interface MutableVocabulary extends Vocabulary {

  /**
   * Add a new score entry for a given term
   * @param term The term related to the entry
   * @param entry The entry score
   */
  void putEntry(String term, Entry entry);

}
