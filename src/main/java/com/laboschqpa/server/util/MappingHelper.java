package com.laboschqpa.server.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.function.Function;

public class MappingHelper {
    /**
     * Converts the given collection to a HashMap.
     */
    public static <K, V> HashMap<K, V> toMap(Collection<V> items, Function<V, K> keyGenerator) {
        return toMap(items, keyGenerator, Function.identity());
    }

    /**
     * Converts the given collection to a HashMap.
     */
    public static <K, V, Gen> HashMap<K, Gen> toMap(Collection<V> items, Function<V, K> keyGenerator, Function<V, Gen> valueGenerator) {
        HashMap<K, Gen> out = new HashMap<>(items.size());
        for (V item : items) {
            out.put(keyGenerator.apply(item), valueGenerator.apply(item));
        }
        return out;
    }
}
