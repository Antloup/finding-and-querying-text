package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.UnsafeSupplier;
import org.jetbrains.annotations.NotNull;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class Result<T, E extends Exception> {

    private Result() {
    }

    public static <T, E extends Exception> Ok<T, E> ok(T ok) {
        return new Result.Ok<>(ok);
    }

    public static <T, E extends Exception> Err<T, E> err(E err) {
        return new Result.Err<>(err);
    }

    public abstract Optional<T> ok();

    public abstract Optional<E> err();

    public abstract boolean isOk();

    public abstract boolean isErr();

    public static <T> Result<T, Exception> Try(UnsafeSupplier<T> supplier) {
        try {
            return new Result.Ok<>(supplier.get());
        } catch (Exception e) {
            return new Result.Err<>(e);
        }
    }

    public abstract T unwrap();

    public abstract <R> R fold(@NotNull Function<T, R> ifOk, @NotNull Function<E, R> ifErr);
    public abstract void foldC(@NotNull Consumer<T> ifOk, @NotNull Consumer<E> ifErr);

    public abstract <R> Result<R, E> map(@NotNull Function<T, R> map);

    public abstract <R> Result<R, E> flatMap(@NotNull Function<T, Result<R, E>> flatMap);

    public static class Ok<T, E extends Exception> extends Result<T, E> {

        private final T ok;

        Ok(@NotNull T ok) {
            super();
            this.ok = ok;
        }

        @Override
        public Optional<T> ok() {
            return Optional.of(ok);
        }

        @Override
        public Optional<E> err() {
            return Optional.empty();
        }

        @Override
        public boolean isOk() {
            return true;
        }

        @Override
        public boolean isErr() {
            return false;
        }

        @Override
        public T unwrap() {
            return ok;
        }

        @Override
        public <R> Result<R, E> map(@NotNull Function<T, R> map) {
            return Result.ok(map.apply(ok));
        }

        @Override
        public <R> Result<R, E> flatMap(@NotNull Function<T, Result<R, E>> flatMap) {
            return flatMap.apply(ok);
        }

        @Override
        public <R> R fold(@NotNull Function<T, R> ifOk, @NotNull Function<E, R> ifErr) {
            return ifOk.apply(ok);
        }

        @Override
        public void foldC(@NotNull Consumer<T> ifOk, @NotNull Consumer<E> ifErr) {
            ifOk.accept(ok);
        }
    }

    public static class Err<T, E extends Exception> extends Result<T, E> {

        private final E err;

        Err(@NotNull E err) {
            super();
            this.err = err;
        }

        @Override
        public Optional<T> ok() {
            return Optional.empty();
        }

        @Override
        public Optional<E> err() {
            return Optional.of(err);
        }

        @Override
        public boolean isOk() {
            return false;
        }

        @Override
        public boolean isErr() {
            return true;
        }

        @Override
        public T unwrap() {
            throw new RuntimeException(err);
        }

        @Override
        public <R> Result<R, E> map(@NotNull Function<T, R> map) {
            return (Result<R, E>) this;
        }

        @Override
        public <R> Result<R, E> flatMap(@NotNull Function<T, Result<R, E>> flatMap) {
            return (Result<R, E>) this;
        }

        @Override
        public <R> R fold(@NotNull Function<T, R> ifOk, @NotNull Function<E, R> ifErr) {
            return ifErr.apply(err);
        }

        @Override
        public void foldC(@NotNull Consumer<T> ifOk, @NotNull Consumer<E> ifErr) {
            ifErr.accept(err);
        }
    }

}
