package cn.myperf4j.base.test;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;
import cn.myperf4j.base.util.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import static cn.myperf4j.base.constant.PropertyKeys.PRO_FILE_NAME;
import static cn.myperf4j.base.constant.PropertyValues.DEFAULT_PRO_FILE;

/**
 * Created by LinShunkang on 2018/10/28
 */
public abstract class BaseTest {

    public static final String TEMP_FILE = "/tmp/MyPerf4J.properties";

    public static final String APP_NAME = "MyPerf4JBaseTest";

    public static final String METRICS_EXPORTER = Metrics.EXPORTER_LOG_STDOUT;

    public static final String INCLUDE_PACKAGES = "MyPerf4J";

    public static final int MILLI_TIMES_LICE = 1000;

    @BeforeAll
    public static void init() {
        System.setProperty(PRO_FILE_NAME, TEMP_FILE);
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(TEMP_FILE, 1);
        writer.write("AppName=" + APP_NAME + "\n");
        writer.write("metrics.exporter=" + METRICS_EXPORTER + "\n");
        writer.write("IncludePackages=" + INCLUDE_PACKAGES + "\n");
        writer.write("MilliTimeSlice=" + MILLI_TIMES_LICE + "\n");
        writer.closeFile(true);

        new File(TEMP_FILE).deleteOnExit();

        initProperties();
    }

    private static void initProperties() {
        try (InputStream in = Files.newInputStream(Paths.get(System.getProperty(PRO_FILE_NAME, DEFAULT_PRO_FILE)))) {
            Properties properties = new Properties();
            properties.load(in);
            MyProperties.initial(properties);
        } catch (IOException e) {
            Logger.error("BaseTest.initProperties()", e);
        }
    }

    @AfterEach
    public void clean() {
        new File(TEMP_FILE).delete();
    }
}
