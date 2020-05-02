package cn.myperf4j.base.metric.processor.influx;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.metric.JvmFileDescriptorMetrics;
import cn.myperf4j.base.metric.processor.AbstractJvmFileDescProcessor;

/**
 * Created by LinShunkang on 2019/11/09
 */
public class InfluxJvmFileDescMetricsProcessor extends AbstractJvmFileDescProcessor {

    private static final ThreadLocal<StringBuilder> SB_TL = new ThreadLocal<StringBuilder>() {
        @Override
        protected StringBuilder initialValue() {
            return new StringBuilder(128);
        }
    };

    @Override
    public void process(JvmFileDescriptorMetrics metrics, long processId, long startMillis, long stopMillis) {
        StringBuilder sb = SB_TL.get();
        try {
            logger.log(createLineProtocol(metrics, startMillis * 1000 * 1000L, sb));
        } finally {
            sb.setLength(0);
        }
    }

    private String createLineProtocol(JvmFileDescriptorMetrics metrics, long startNanos, StringBuilder sb) {
        sb.append("jvm_file_descriptor_metrics")
                .append(",AppName=").append(ProfilingConfig.getInstance().getAppName())
                .append(" OpenCount=").append(metrics.getOpenCount()).append('i')
                .append(",OpenPercent=").append(metrics.getOpenPercent())
                .append(' ').append(startNanos);
        return sb.toString();
    }

}
