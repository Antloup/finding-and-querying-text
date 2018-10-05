package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.*;

public class Vocabulary {

  private SortedMap<String, List<Entry>> data;

  public Vocabulary() {
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

}
