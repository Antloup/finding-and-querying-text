package com.github.quadinsa5if.findingandqueryingtext.util;

import com.github.quadinsa5if.findingandqueryingtext.lang.UnsafeFunction;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class Cache<K, V> extends LinkedHashMap<K, V> {

  private final int capacity;
  transient private final Function<K, V> loader;

  public Cache(int capacity, Function<K, V> loader) {
    this.capacity = capacity;
    this.loader = loader;
  }

  @Override
  protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
    return size() > capacity;
  }

  @Override
  @Deprecated
  public V get(Object key) {
    throw new RuntimeException("Invalid access");
  }

  public V getOrLoad(K key) {
    if (!this.containsKey(key)) {
      try {
        put(key, loader.apply(key));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
    return super.get(key);
  }


}
