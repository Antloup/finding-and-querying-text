package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.Objects;

public class ArticleId {

  public final int id;

  public ArticleId(int id) {
    this.id = id;
  }

  public int id() {
    return id;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ArticleId)) return false;
    ArticleId articleId = (ArticleId) o;
    return id == articleId.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return "ArticleId{" +
        "id=" + id +
        '}';
  }
}
