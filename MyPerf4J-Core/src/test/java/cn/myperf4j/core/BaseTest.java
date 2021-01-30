package cn.myperf4j.core;

import cn.myperf4j.base.config.MyProperties;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.constant.PropertyValues.Metrics;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;
import cn.myperf4j.base.util.io.IOUtils;
import cn.myperf4j.base.util.Logger;
import org.junit.BeforeClass;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by LinShunkang on 2020/05/31
 */
public abstract class BaseTest {

    @BeforeClass
    public static void doInit() {
        String propertiesFile = "/tmp/MyPerf4J.properties";
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("app_name=Test\n");
        writer.write("metrics.exporter=" + Metrics.EXPORTER_LOG_INFLUX_DB + "\n");
        writer.write("filter.packages.include=MyPerf4J\n");
        writer.write("metrics.time_slice.method=1000\n");
        writer.write("metrics.time_slice.jvm=1000\n");
        writer.closeFile(true);

        new File(propertiesFile).deleteOnExit();
    }

    protected static void initProperties() {
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
