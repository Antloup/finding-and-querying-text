package com.github.quadinsa5if.findingandqueryingtext.model;

public class ReversedIndexIdentifier {

  public final int offset;
  public final int length;
  public String term="";

  public ReversedIndexIdentifier(int offset, int length) {
    this.offset = offset;
    this.length = length;
  }
  public ReversedIndexIdentifier(int offset, int length, String term) {
    this.offset = offset;
    this.length = length;
    this.term  = term;
  }
}
