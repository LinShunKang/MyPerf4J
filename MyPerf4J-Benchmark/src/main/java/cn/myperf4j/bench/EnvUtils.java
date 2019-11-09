package cn.myperf4j.bench;

import cn.myperf4j.asm.ASMBootstrap;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;

/**
 * Created by LinShunkang on 2019/08/31
 */
public class EnvUtils {

    public static synchronized boolean initASMBootstrap(long milliTimeSlice) {
        initProperties(milliTimeSlice);
        return ASMBootstrap.getInstance().initial();
    }

    private static void initProperties(long milliTimeSlice) {
        String propertiesFile = "/tmp/MyPerf4J.properties";
        buildPropertiesFile(propertiesFile, milliTimeSlice);
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
    }

    private static void buildPropertiesFile(String propertiesFile, long milliTimeSlice) {
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("AppName=MyPerf4JTest\n");
        writer.write("MetricsProcessorType=" + PropertyValues.METRICS_PROCESS_TYPE_LOGGER + "\n");
        writer.write("MethodMetricsFile=/tmp/method_metrics.log\n");
        writer.write("IncludePackages=MyPerf4J\n");
        writer.write("MillTimeSlice=" + milliTimeSlice + "\n");
        writer.closeFile(true);
    }
}
