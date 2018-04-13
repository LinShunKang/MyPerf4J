package cn.perf4j.util;

import java.util.Properties;

/**
 * Created by LinShunkang on 2018/4/9
 */
public final class MyProperties {

    private static Properties properties;

    public static synchronized boolean initial(Properties prop) {
        if (properties != null || prop == null) {
            return false;
        }

        properties = prop;
        return true;
    }

    public static String getStr(String key) {
        checkState();

        String value = System.getProperty(key);
        if (value != null) {
            return value;
        }
        return properties.getProperty(key);
    }

    private static void checkState() {
        if (properties == null) {
            throw new IllegalStateException("MyProperties is not initial yet!!!");
        }
    }

    public static void setStr(String key, String value) {
        checkState();

        System.setProperty(key, value);
        properties.setProperty(key, value);
    }

    public static String getStr(String key, String defaultValue) {
        checkState();

        String result = getStr(key);
        if (result != null) {
            return result;
        }
        return defaultValue;
    }

    public static long getLong(String key, long defaultValue) {
        checkState();

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
        checkState();

        long result = getLong(key, defaultValue);
        if (result <= minValue) {
            return minValue;
        }
        return result;
    }

    public static boolean isSame(String key, String expectValue) {
        checkState();

        if (expectValue == null) {
            throw new IllegalArgumentException("isSame(" + key + ", null): expectValue must not null!!!");
        }
        return expectValue.equals(getStr(key));
    }
}
