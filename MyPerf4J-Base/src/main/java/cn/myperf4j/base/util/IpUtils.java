package cn.myperf4j.base.util;

import java.net.InetAddress;

/**
 * Created by LinShunkang on 2020/05/23
 */
public class IpUtils {

    private static String LOCALHOST_NAME = "";

    static {
        try {
            InetAddress localInetAddress = InetAddress.getLocalHost();
            LOCALHOST_NAME = localInetAddress.getHostName();
            Logger.info("IpUtils.static localHostName=" + LOCALHOST_NAME);
        } catch (Exception e) {
            Logger.error("IpUtils.static initializer()", e);
        }
    }

    public static String getLocalhostName() {
        return LOCALHOST_NAME;
    }
}
