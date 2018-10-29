package com.github.quadinsa5if.findingandqueryingtext.model;

public class ReversedIndexIdentifier {

  public final int offset;
  public final int length;
  public final String term;

  public ReversedIndexIdentifier(int offset, int length) {
    this.offset = offset;
    this.length = length;
    this.term = "";
  }
  public ReversedIndexIdentifier(int offset, int length, String term) {
    this.offset = offset;
    this.length = length;
    this.term  = term;
  }

  @Override
  public String toString() {
    return "ReversedIndexIdentifier(" + offset + ", " + length + ", " + term + ")";
  }
}
