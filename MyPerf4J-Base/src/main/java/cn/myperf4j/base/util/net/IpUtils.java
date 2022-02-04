package cn.myperf4j.base.util.net;

import cn.myperf4j.base.util.Logger;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.Enumeration;

/**
 * Created by LinShunkang on 2020/05/23
 */
public final class IpUtils {

    private static String localIp = "127.0.0.1";

    private static String localHostName = "localhost";

    static {
        try {
            final InetAddress innerInetAddr = getInnerInetAddr();
            localIp = innerInetAddr.getHostAddress();
            localHostName = innerInetAddr.getHostName();
            Logger.info("IpUtils:1 localIp=" + localIp + ", localHostName=" + localHostName);
        } catch (Throwable t) {
            Logger.error("IpUtils.static1 initializer()", t);
        }

        try {
            final InetAddress localInetAddress = InetAddress.getLocalHost();
            if (!isInnerIpv4(localIp)) {
                localIp = localInetAddress.getHostAddress();
                localHostName = localInetAddress.getHostName();
                Logger.info("IpUtils:2 localIp=" + localIp + ", localHostName=" + localHostName);
            } else if (localIp.equals(localHostName)) {
                localHostName = localInetAddress.getHostName();
                Logger.info("IpUtils:3 localIp=" + localIp + ", localHostName=" + localHostName);
            }
        } catch (Throwable t) {
            Logger.error("IpUtils.static2 initializer()", t);
        }
    }

    private static InetAddress getInnerInetAddr() throws UnknownHostException {
        try {
            final Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
            while (netInterfaces.hasMoreElements()) {
                final NetworkInterface ni = netInterfaces.nextElement();
                final Enumeration<InetAddress> ips = ni.getInetAddresses();
                while (ips.hasMoreElements()) {
                    final InetAddress inetAddress = ips.nextElement();
                    if (isInnerIpv4(inetAddress)) {
                        return inetAddress;
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("IpUtils.getInnerInetAddr(): catch Exception!", e);
        }
        return InetAddress.getLocalHost();
    }

    private static boolean isInnerIpv4(String ipAddr) {
        try {
            return isInnerIpv4(InetAddress.getByName(ipAddr));
        } catch (UnknownHostException e) {
            Logger.error("IpUtils.isInnerIpv4(" + ipAddr + ")", e);
        }
        return false;
    }

    private static boolean isInnerIpv4(final InetAddress inetAddress) {
        return inetAddress instanceof Inet4Address && inetAddress.isSiteLocalAddress();
    }

    public static String getLocalIp() {
        return localIp;
    }

    public static String getLocalhostName() {
        return localHostName;
    }

    private IpUtils() {
        //empty
    }
}
