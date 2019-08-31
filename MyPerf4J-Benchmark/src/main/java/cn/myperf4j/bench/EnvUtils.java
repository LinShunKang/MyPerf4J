package cn.myperf4j.bench;

import cn.myperf4j.asm.ASMBootstrap;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.file.AutoRollingFileWriter;
import cn.myperf4j.base.util.file.MinutelyRollingFileWriter;

/**
 * Created by LinShunkang on 2019/08/31
 */
public class EnvUtils {

    public static boolean initASMBootstrap() {
        initProperties();
        return ASMBootstrap.getInstance().initial();
    }

    private static void initProperties() {
        String propertiesFile = "/tmp/MyPerf4J.properties";
        buildPropertiesFile(propertiesFile);
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
    }

    private static void buildPropertiesFile(String propertiesFile) {
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("AppName=MyPerf4JTest\n");
        writer.write("MetricsProcessorType=" + PropertyValues.METRICS_PROCESS_TYPE_STDOUT + "\n");
        writer.write("IncludePackages=MyPerf4J\n");
        writer.write("MillTimeSlice=1000\n");
        writer.closeFile(true);
    }
}
