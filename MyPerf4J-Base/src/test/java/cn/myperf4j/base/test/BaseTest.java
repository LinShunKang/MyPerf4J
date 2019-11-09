package cn.myperf4j.base.test;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.IOUtils;
import cn.myperf4j.base.util.Logger;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by LinShunkang on 2018/10/28
 */
public abstract class BaseTest {

    public static final String TEMP_FILE = "/tmp/MyPerf4J.properties";

    public static final String APP_NAME = "MyPerf4JBaseTest";

    public static final int METRICS_PROCESSOR_TYPE = 1;

    public static final String INCLUDE_PACKAGES = "MyPerf4J";

    public static final int MILLI_TIMES_LICE = 1000;

    @BeforeClass
    public static void init() {
        System.setProperty(PropertyKeys.PRO_FILE_NAME, TEMP_FILE);
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(TEMP_FILE, 1);
        writer.write("AppName=" + APP_NAME + "\n");
        writer.write("MetricsProcessorType=" + METRICS_PROCESSOR_TYPE + "\n");
        writer.write("IncludePackages=" + INCLUDE_PACKAGES + "\n");
        writer.write("MilliTimeSlice=" + MILLI_TIMES_LICE + "\n");
        writer.closeFile(true);

        new File(TEMP_FILE).deleteOnExit();

        initProperties();
    }

    private static void initProperties() {
        InputStream in = null;
        try {
            in = new FileInputStream(System.getProperty(PropertyKeys.PRO_FILE_NAME, PropertyValues.DEFAULT_PRO_FILE));

            Properties properties = new Properties();
            properties.load(in);
            MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("BaseTest.initProperties()", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

}
