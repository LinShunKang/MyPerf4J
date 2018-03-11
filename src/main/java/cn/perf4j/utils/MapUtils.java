package cn.perf4j.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by LinShunkang on 2018/3/11
 */
public final class MapUtils {

    public static <K, V> Map<K, V> createHashMap(int keyNum) {
        return new HashMap<>(getFitCapacity(keyNum));
    }

    public static int getFitCapacity(int keyNum) {
        return (int) (keyNum / 0.75) + 1;
    }
}
