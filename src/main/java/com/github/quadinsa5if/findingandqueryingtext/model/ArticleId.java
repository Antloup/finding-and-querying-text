package com.github.quadinsa5if.findingandqueryingtext.model;

public class ArticleId {

  public final int id;
  public final String path;

  public ArticleId(int id, String path) {
    this.id = id;
    this.path = path;
  }

  @Override
  public String toString() {
    return "ArticleId{" +
        "id=" + id +
        ", path='" + path + '\'' +
        '}';
  }
}
