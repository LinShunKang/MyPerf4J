package cn.myperf4j.base.util;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;

/**
 * Created by LinShunkang on 2020/12/06
 */
public final class NetUtils {

    public static final int MIN_PORT_NUMBER = 1;

    public static final int MAX_PORT_NUMBER = 65535;

    public static boolean isPortAvailable(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid port: " + port);
        }

        try (ServerSocket ss = new ServerSocket(port); DatagramSocket ds = new DatagramSocket(port)) {
            ss.setReuseAddress(true);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private NetUtils() {
        //empty
    }
}
