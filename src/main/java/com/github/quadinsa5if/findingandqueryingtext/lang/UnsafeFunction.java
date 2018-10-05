package com.github.quadinsa5if.findingandqueryingtext.lang;

public interface UnsafeFunction<I, O, E extends Exception> {
  O apply(I input) throws E;
}
