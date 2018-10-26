package com.github.quadinsa5if.findingandqueryingtext.model;

public class Article {

  public final String content;
  public final ArticleHeader articleHeader;

  public Article(ArticleHeader articleHeader, String content) {
    this.articleHeader = articleHeader;
    this.content = content;
  }
}
