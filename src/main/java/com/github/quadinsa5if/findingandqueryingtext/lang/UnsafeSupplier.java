package com.github.quadinsa5if.findingandqueryingtext.lang;

public interface UnsafeSupplier<T> {
  T get() throws Exception;
}
