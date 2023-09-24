package cn.myperf4j.base.metric.collector;

import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import com.sun.management.UnixOperatingSystemMXBean;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;

/**
 * Created by LinShunkang on 2019/11/03
 */
public final class JvmFileDescCollector {

    private static final OperatingSystemMXBean SYSTEM_MX_BEAN = ManagementFactory.getOperatingSystemMXBean();

    private JvmFileDescCollector() {
        //empty
    }

    public static JvmFileDescriptorMetrics collectFileDescMetrics() {
        if (SYSTEM_MX_BEAN instanceof UnixOperatingSystemMXBean) {
            final UnixOperatingSystemMXBean unixMXBean = (UnixOperatingSystemMXBean) SYSTEM_MX_BEAN;
            return new JvmFileDescriptorMetrics(unixMXBean.getOpenFileDescriptorCount(),
                    unixMXBean.getMaxFileDescriptorCount());
        }
        return new JvmFileDescriptorMetrics(0L, 0L);
    }
}
