package com.github.quadinsa5if.findingandqueryingtext.model;

public class Entry {

  public final ArticleId articleId;
  public final float score;

  public Entry(ArticleId articleId, float score) {
    this.articleId = articleId;
    this.score = score;
  }

  @Override
  public String toString() {
    return "Entry{" +
        "articleId=" + articleId +
        ", score=" + score +
        '}';
  }
}
