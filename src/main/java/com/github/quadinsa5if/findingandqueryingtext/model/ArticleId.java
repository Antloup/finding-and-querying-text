package com.github.quadinsa5if.findingandqueryingtext.model;

public class ArticleId {

  public final String name;
  public final int line;
  public final String path;

  public ArticleId(String name, int line, String path) {
    this.name = name;
    this.line = line;
    this.path = path;
  }
}
