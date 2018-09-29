package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;

public interface Tokenizer<I, O, E extends Exception> {
  Iter<O> tokenize(I input) throws E;
}
