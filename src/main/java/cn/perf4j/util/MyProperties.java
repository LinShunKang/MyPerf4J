package cn.perf4j.util;

import cn.perf4j.AsyncPerfStatsProcessor;
import cn.perf4j.PropConstants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by LinShunkang on 2018/4/9
 */
public final class MyProperties {

    private static Properties properties = new Properties();

    static {
        InputStream in = null;
        try {
            in = AsyncPerfStatsProcessor.class.getClassLoader().getResourceAsStream(PropConstants.PRO_FILE_NAME);
            if (in != null) {
                properties.load(in);
            }
        } catch (IOException e) {
            Logger.error("MyProperties load config/myPerf4J.properties error!!!", e);
        } finally {
            IOUtils.closeQuietly(in);
        }
    }

    public static String getStr(String key) {
        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        return properties.getProperty(key);
    }

    public static void setStr(String key, String value) {
        System.setProperty(key, value);
        properties.setProperty(key, value);
    }

    public static String getStr(String key, String defaultValue) {
        String result = getStr(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    public static long getLong(String key, long defaultValue) {
        String result = getStr(key);
        if (result == null) {
            return defaultValue;
        }

        try {
            return Long.parseLong(result);
        } catch (Exception e) {
            Logger.error("MyProperties.getProperty(" + key + ", " + defaultValue + ")", e);
        }
        return defaultValue;
    }

    public static long getLong(String key, long defaultValue, long minValue) {
        long result = getLong(key, defaultValue);
        if (result <= minValue) {
            return minValue;
        }
        return result;
    }

    public static boolean isSame(String key, String expectValue) {
        if (expectValue == null) {
            throw new IllegalArgumentException("isSame(" + key + ", null): expectValue must not null!!!");
        }
        return expectValue.equals(getStr(key));
    }
}
