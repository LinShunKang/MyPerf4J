package cn.myperf4j.base.util;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/3/11
 */
public final class MapUtils {

    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    public static <K, V> Map<K, V> createHashMap(int keyNum) {
        return new HashMap<>(getFitCapacity(keyNum));
    }

    public static <K, V> Map<K, V> createHashMap(int keyNum, float loadFactor) {
        return new HashMap<>(getFitCapacity(keyNum, loadFactor));
    }

    public static <K, V> ConcurrentHashMap<K, V> createConcHashMap(int keyNum, float loadFactor) {
        return new ConcurrentHashMap<>(getFitCapacity(keyNum, loadFactor));
    }

    public static <K, V> Map<K, V> createLinkedHashMap(int keyNum) {
        return new LinkedHashMap<>(getFitCapacity(keyNum));
    }

    public static int getFitCapacity(int keyNum) {
        return getFitCapacity(keyNum, DEFAULT_LOAD_FACTOR);
    }

    public static int getFitCapacity(int keyNum, float loadFactor) {
        return (int) (keyNum / loadFactor) + 1;
    }

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }
}
