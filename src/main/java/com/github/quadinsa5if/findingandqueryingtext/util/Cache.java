package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.UnsafeFunction;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<K, V> extends LinkedHashMap<K, V> {

  private final int capacity;
  private final UnsafeFunction<K, V, Exception> fetch;

  public Cache(int capacity, UnsafeFunction<K, V, Exception> fetch) {
    this.capacity = capacity;
    this.fetch = fetch;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity;
  }

  @Override
  @SuppressWarnings("unchecked")
  public V get(Object key) {
    if (!this.containsKey(key)) {
      try {
        put((K) key, fetch.apply((K) key));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return super.get(key);
  }


}
