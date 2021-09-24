package com.laboschqpa.server.util;

import java.util.Collection;
import java.util.HashSet;

public class CollectionHelpers {

    public static <T> HashSet<T> subtractToSet(Collection<T> whole, Collection<T> toSubtract) {
        final HashSet<T> toSubtractSet = new HashSet<>(toSubtract);

        final HashSet<T> result = new HashSet<>(Math.min(whole.size() - toSubtract.size(), 0));
        for (T itemFromWhole : whole) {
            if (!toSubtractSet.contains(itemFromWhole)) {
                result.add(itemFromWhole);
            }
        }

        return result;
    }

}
