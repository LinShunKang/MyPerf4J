package cn.myperf4j.base.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by LinShunkang on 2020/05/16
 */
public final class InputStreamUtils {

    private static final ThreadLocal<ByteArrayOutputStream> OP_TL = new ThreadLocal<ByteArrayOutputStream>() {
        @Override
        protected ByteArrayOutputStream initialValue() {
            return new ByteArrayOutputStream(4096);
        }
    };

    private static final ThreadLocal<byte[]> BYTES_TL = new ThreadLocal<byte[]>() {
        @Override
        protected byte[] initialValue() {
            return new byte[1024];
        }
    };

    public static String toString(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return null;
        }

        ByteArrayOutputStream result = OP_TL.get();
        byte[] buffer = BYTES_TL.get();
        try {
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            return result.toString("UTF-8");
        } finally {
            result.reset();
        }
    }
}
