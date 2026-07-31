package cn.myperf4j.bench;

import cn.myperf4j.asm.ASMBootstrap;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;

import static cn.myperf4j.base.constant.PropertyValues.Metrics.EXPORTER_LOG_STDOUT;

/**
 * Created by LinShunkang on 2019/08/31
 */
public final class EnvUtils {

    private EnvUtils() {
        //empty
    }

    public static synchronized boolean initASMBootstrap(long milliTimeSlice) {
        initProperties(milliTimeSlice);
        return ASMBootstrap.getInstance().initial();
    }

    private static void initProperties(long milliTimeSlice) {
        final String propertiesFile = "/tmp/MyPerf4J.properties";
        buildPropertiesFile(propertiesFile, milliTimeSlice);
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
    }

    private static void buildPropertiesFile(String propertiesFile, long milliTimeSlice) {
        final AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("app_name=MyPerf4JTest\n");
        writer.write("metrics.exporter=" + EXPORTER_LOG_STDOUT + "\n");
        writer.write("filter.packages.include=MyPerf4J\n");
        writer.write("metrics.time_slice.method=" + milliTimeSlice + "\n");
        writer.write("metrics.time_slice.jvm=" + milliTimeSlice + "\n");
        writer.closeFile(true);
    }
}
