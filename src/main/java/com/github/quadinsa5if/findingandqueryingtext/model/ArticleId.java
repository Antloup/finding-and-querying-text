package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.Objects;

public class ArticleId {

  public final int id;
  public final String path;

  public ArticleId(int id, String path) {
    this.id = id;
    this.path = path;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArticleId)) return false;
    ArticleId articleId = (ArticleId) o;
    return id == articleId.id &&
        Objects.equals(path, articleId.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, path);
  }

  @Override
  public String toString() {
    return "ArticleId{" +
        "id=" + id +
        ", path='" + path + '\'' +
        '}';
  }
}
