package cn.myperf4j.base.util.io;

import cn.myperf4j.base.io.Bytes;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by LinShunkang on 2026/06/19
 */
public final class BytesUtils {

    private static final ConcurrentMap<String, Bytes> BYTES_CACHE = new ConcurrentHashMap<>(64 * 1024);

    public static Bytes toCachedBytes(String str) {
        final Bytes bytes = BYTES_CACHE.get(str); //Fast Path
        return bytes != null ? bytes : BYTES_CACHE.computeIfAbsent(str, Bytes::copy);
    }

    private BytesUtils() {
    }
}
