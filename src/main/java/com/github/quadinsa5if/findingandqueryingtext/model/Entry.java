package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.Objects;

public class Entry {

    public final int articleId;
    public final double score;

    public Entry(int articleId, double score) {
        this.articleId = articleId;
        this.score = score;
    }

    public int articleId() {
        return articleId;
    }

    public double score() {
        return score;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "articleId=" + articleId +
                ", score=" + score +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entry entry = (Entry) o;
        return Double.compare(entry.score, score) == 0 &&
                Objects.equals(articleId, entry.articleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(articleId, score);
    }
}
