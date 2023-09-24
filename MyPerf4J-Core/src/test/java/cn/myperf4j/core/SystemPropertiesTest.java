package cn.myperf4j.core;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;

/**
 * Created by LinShunkang on 2019/07/28
 */
public class SystemPropertiesTest {

    @Test
    public void test() {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getProperty("path.separator"));
        System.out.println(System.getProperty("line.separator"));
    }

    @Test
    public void testParseConfigFileDir() {
        // windows下os.name的返回值是以 Windows 开头的
        final String osName = System.getProperty("os.name");
        Assert.assertFalse(osName.startsWith("windows"));

        final boolean isWindowsOS = osName.toLowerCase().startsWith("windows");
        if (isWindowsOS) {
            final String configFilePath = "C:\\path\\to\\MyPerf4J.properties";
            final int idx = configFilePath.lastIndexOf(File.separatorChar);
            Assert.assertEquals(configFilePath.substring(0, idx + 1), "C:\\path\\to\\");
        } else {
            final String configFilePath = "/path/to/MyPerf4J.properties";
            final int idx = configFilePath.lastIndexOf(File.separatorChar);
            Assert.assertEquals(configFilePath.substring(0, idx + 1), "/path/to/");
        }
    }
}
