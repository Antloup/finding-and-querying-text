package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.UnsafeSupplier;

import java.util.NoSuchElementException;
import java.util.Optional;

public class Result<T, E extends Exception> {

  private T ok;
  private E err;

  private Result(T ok, E err) {
    this.ok = ok;
    this.err = err;
  }

  static class Ok<T, E extends Exception> extends Result<T, E> {
    public Ok(T ok) {
      super(ok, null);
    }
  }

  static class Err<T, E extends Exception> extends Result<T, E> {
    public Err(E err) {
      super(null, err);
    }
  }

  public Optional<T> ok() {
    return Optional.ofNullable(ok);
  }

  public Optional<E> err() {
    return Optional.ofNullable(err);
  }

  public static <T> Result<T, Exception> wrap(UnsafeSupplier<T> supplier) {
    try {
      return new Result.Ok<>(supplier.get());
    } catch (Exception e) {
      return new Result.Err<>(e);
    }
  }

  public T unwrap() {
    if (ok != null) {
      return ok;
    } else {
      throw new NoSuchElementException();
    }
  }

}
