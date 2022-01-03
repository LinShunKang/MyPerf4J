package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmBufferPoolMetrics;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by LinShunkang on 2022/01/03
 */
public final class JvmBufferPoolCollector {

    private JvmBufferPoolCollector() {
        //empty
    }

    public static List<JvmBufferPoolMetrics> collectBufferPoolMetrics() {
        final List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        final List<JvmBufferPoolMetrics> result = new ArrayList<>(pools.size());
        for (int i = 0, size = pools.size(); i < size; i++) {
            result.add(new JvmBufferPoolMetrics(pools.get(i)));
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(collectBufferPoolMetrics());
    }
}
