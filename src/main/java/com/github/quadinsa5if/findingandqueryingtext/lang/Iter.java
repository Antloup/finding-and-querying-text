package com.github.quadinsa5if.findingandqueryingtext.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Optional;

public interface Iter<T> extends Iterable<T> {

  Optional<T> next();

  @NotNull
  @Override
  default Iterator<T> iterator() {
    return new Iterator<T>() {
      private Optional<T> current = Iter.this.next();

      @Override
      public boolean hasNext() {
        return current.isPresent();
      }

      @Override
      public T next() {
        T value = current.get();
        current = Iter.this.next();
        return value;
      }
    };
  }
}
