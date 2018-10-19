package com.github.quadinsa5if.findingandqueryingtext.lang;

import com.github.quadinsa5if.findingandqueryingtext.util.Result;

import java.io.IOException;
import java.util.function.Function;

public interface IO<T> {

    T sync() throws IOException;

    default Result<T, IOException> attempt() {
        try {
            return Result.ok(sync());
        } catch (IOException e) {
            return Result.err(e);
        }
    }

    default <R> IO<R> map(Function<T, R> transform) {
        final IO<T> self = this;
        return () -> transform.apply(self.sync());
    }

    default <R> IO<R> flatMap(Function<T, IO<R>> transform) {
        try {
            return transform.apply(sync());
        } catch (IOException ioe) {
            return () -> {
                throw ioe;
            };
        }
    }

    default <R> IO<R> then(IO<R> otherIo) {
        final IO self = this;
        return () -> {
            self.sync();
            return otherIo.sync();
        };
    }


}
