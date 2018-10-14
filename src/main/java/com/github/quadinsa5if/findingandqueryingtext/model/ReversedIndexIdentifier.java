package com.github.quadinsa5if.findingandqueryingtext.model;

public class ReversedIndexIdentifier {

  public final int offset;
  public final int length;
  public final String term;

  public ReversedIndexIdentifier(String term, int offset, int length) {
    this.offset = offset;
    this.length = length;
    this.term = term;
  }

}
