package cn.myperf4j.base.util.io;

import cn.myperf4j.base.util.Logger;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by LinShunkang on 2018/4/9
 */
public final class IOUtils {

    private IOUtils() {
        //empty
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {
            Logger.warn("IOUtils.closeQuietly(): " + e.getMessage());
        }
    }
}
