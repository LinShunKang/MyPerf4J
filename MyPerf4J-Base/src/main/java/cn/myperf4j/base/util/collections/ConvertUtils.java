package cn.myperf4j.base.util.collections;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.myperf4j.base.util.collections.CollectionUtils.isNotEmpty;
import static java.util.Collections.emptyMap;
import static java.util.stream.Collectors.groupingBy;

/**
 * Created by LinShunkang on 2026/07/07
 */
public final class ConvertUtils {

    public static <T, R> List<R> toList(List<T> list, Function<? super T, ? extends R> mapper) {
        return toStream(list, mapper).collect(Collectors.toList());
    }

    public static <T, R> Stream<R> toStream(Collection<T> list, Function<? super T, ? extends R> mapper) {
        return isNotEmpty(list) ? list.stream().map(mapper) : Stream.empty();
    }

    public static <T, K> Map<K, List<T>> groupBy(Collection<T> sourceList,
                                                 Function<? super T, ? extends K> classifier) {
        return isNotEmpty(sourceList) ? sourceList.stream().collect(groupingBy(classifier)) : emptyMap();
    }

    public static <T, K, A, D> Map<K, D> groupBy(Collection<T> sourceList,
                                                 Function<? super T, ? extends K> classifier,
                                                 Collector<? super T, A, D> downstream) {
        return isNotEmpty(sourceList) ? sourceList.stream().collect(groupingBy(classifier, downstream)) : emptyMap();
    }

    private ConvertUtils() {
        //empty
    }
}
