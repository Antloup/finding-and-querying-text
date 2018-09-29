package com.github.quadinsa5if.findingandqueryingtext.lang;

import java.util.Optional;

public interface Iter<T> {
  Optional<T> next();
}
