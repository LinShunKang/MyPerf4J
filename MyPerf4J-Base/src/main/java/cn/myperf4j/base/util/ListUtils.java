package cn.myperf4j.base.util;

import java.util.List;

/**
 * Created by LinShunkang on 2020/05/16
 */
public final class ListUtils {

    public static <T> boolean isEmpty(List<T> list) {
        return list == null || list.isEmpty();
    }

    public static <T> boolean isNotEmpty(List<T> list) {
        return !isEmpty(list);
    }
}
