package cn.myperf4j.base.util.collections;

import java.util.Collection;

/**
 * Created by LinShunkang on 2026/07/07
 */
public final class CollectionUtils {

    public static <T> boolean isEmpty(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }

    public static <T> boolean isNotEmpty(Collection<T> collection) {
        return !isEmpty(collection);
    }

    public static <T> int size(Collection<T> collection) {
        return collection != null ? collection.size() : 0;
    }

    private CollectionUtils() {
        //empty
    }
}
