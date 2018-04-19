package cn.myperf4j.core.util;

import java.util.HashMap;
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

    public static int getFitCapacity(int keyNum) {
        return getFitCapacity(keyNum, DEFAULT_LOAD_FACTOR);
    }

    public static int getFitCapacity(int keyNum, float loadFactor) {
        return (int) (keyNum / loadFactor) + 1;
    }
}
