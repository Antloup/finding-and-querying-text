package com.github.quadinsa5if.findingandqueryingtext.tokenizer;

import com.github.quadinsa5if.findingandqueryingtext.lang.Iter;
import org.jetbrains.annotations.NotNull;

public interface Tokenizer<I, O, E extends Exception> {
  Iter<O> tokenize(@NotNull I input) throws E;
}
