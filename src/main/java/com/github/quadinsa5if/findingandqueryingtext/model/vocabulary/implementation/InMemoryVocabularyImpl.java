package com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.implementation;

import com.github.quadinsa5if.findingandqueryingtext.model.Entry;
import com.github.quadinsa5if.findingandqueryingtext.model.vocabulary.MutableVocabulary;

import java.util.*;

/**
 * In memory representation of MutableVocabulary
 */
public class InMemoryVocabularyImpl implements MutableVocabulary {

  private SortedMap<String, List<Entry>> data;

  public InMemoryVocabularyImpl() {
    data = new TreeMap<>();
  }

  public void putEntry(String term, Entry entry) {
    if (data.containsKey(term)) {
      data.get(term).add(entry);
    } else {
      List<Entry> entries = new ArrayList<>();
      entries.add(entry);
      data.put(term, entries);
    }
  }

  public List<Entry> getPostingList(String term) {
    return Optional.of(data.get(term)).orElse(new ArrayList<>());
  }

  @Override
  public List<String> getTerms() {
    return new ArrayList<>(data.keySet());
  }
}
