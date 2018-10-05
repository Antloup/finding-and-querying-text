package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.List;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

public class Vocabulary {

  private SortedMap<String, List<Entry>> data;

  public Vocabulary() {
    data = new TreeMap<>();
  }

}
