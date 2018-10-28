package com.github.quadinsa5if.findingandqueryingtext.model;

import java.util.Objects;

public class ArticleHeader {

    public final int id;
    public final String path;

    //TODO
//    public final String documentId;
//    public final String documentNo;
//    public final String title;

    public ArticleHeader(int id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ArticleHeader that = (ArticleHeader) o;
        return id == that.id &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, path);
    }
}
