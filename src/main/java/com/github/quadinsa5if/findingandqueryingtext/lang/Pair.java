package com.github.quadinsa5if.findingandqueryingtext.lang;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

public class Pair<K, V> {

    public final K first;
    public final V second;

    public Pair(@NotNull K first, @NotNull V second) {
        this.first = first;
        this.second = second;
    }

    public Pair(@NotNull Map.Entry<K, V> entry) {
        Objects.requireNonNull(entry.getKey());
        Objects.requireNonNull(entry.getValue());
        this.first = entry.getKey();
        this.second = entry.getValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return Objects.equals(first, pair.first) &&
                Objects.equals(second, pair.second);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "first=" + first +
                ", second=" + second +
                '}';
    }
}
