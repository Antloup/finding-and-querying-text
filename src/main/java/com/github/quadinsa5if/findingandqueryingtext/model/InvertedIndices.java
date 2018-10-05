package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.List;

public class InvertedIndices {

  private final Vocabulary vocabulary;
  private final List<Entry> postingList;

  public InvertedIndices(Vocabulary vocabulary, List<Entry> postingList) {
    this.vocabulary = vocabulary;
    this.postingList = postingList;
  }

}
