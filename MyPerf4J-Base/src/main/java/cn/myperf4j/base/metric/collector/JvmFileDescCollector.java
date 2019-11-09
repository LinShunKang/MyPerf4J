package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class JvmFileDescCollector {

    public static JvmFileDescriptorMetrics collectFileDescMetrics() {
        OperatingSystemMXBean systemMXBean = ManagementFactory.getOperatingSystemMXBean();
        if (systemMXBean instanceof UnixOperatingSystemMXBean) {
            UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean) systemMXBean;
            return new JvmFileDescriptorMetrics(unixMXBean.getOpenFileDescriptorCount(), unixMXBean.getMaxFileDescriptorCount());
        }
        return new JvmFileDescriptorMetrics(0L, 0L);
    }

}
