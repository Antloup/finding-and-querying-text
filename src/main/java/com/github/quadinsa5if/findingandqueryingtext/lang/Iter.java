package com.github.quadinsa5if.findingandqueryingtext.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface Iter<T> extends Iterable<T> {

    Optional<T> next();

    @NotNull
    @Override
    default Iterator<T> iterator() {
        final Iter<T> self = this;
        return new Iterator<T>() {
            private Optional<T> current = self.next();

            @Override
            public boolean hasNext() {
                return current.isPresent();
            }

            @Override
            public T next() {
                if (current.isPresent()) {
                    T value = current.get();
                    current = Iter.this.next();
                    return value;
                } else {
                    throw new NoSuchElementException();
                }
            }
        };
    }

    static Iter<Byte> over(byte[] bytes) {
        return new Iter<Byte>() {
            int i = 0;
            @Override
            public Optional<Byte> next() {
                if (i < bytes.length) {
                    return Optional.of(bytes[i++]);
                } else {
                    return Optional.empty();
                }
            }
        };
    }

    static Iter<Byte> over(String str) {
        return Iter.over(str.getBytes());
    }

    static <T> Iter<T> over(List<T> list) {
        return new Iter<T>() {
            int i = 0;
            @Override
            public Optional<T> next() {
                if (i < list.size()) {
                    return Optional.of(list.get(i++));
                } else {
                    return Optional.empty();
                }
            }
        };
    }

}
