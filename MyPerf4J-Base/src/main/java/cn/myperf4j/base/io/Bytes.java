package cn.myperf4j.base.io;

import java.nio.charset.Charset;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/08/19
 */
public final class Bytes {

    private final byte[] bytes;

    private final int length;

    private Bytes(byte[] bytes) {
        this(bytes, bytes.length);
    }

    private Bytes(byte[] bytes, int length) {
        this.bytes = bytes;
        this.length = length;
    }

    public byte[] bytes() {
        return bytes;
    }

    public int length() {
        return length;
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }

    public boolean isEmpty() {
        return bytes == null || length <= 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof Bytes)) {
            return false;
        }

        final Bytes that = (Bytes) obj;
        return this.length == that.length
                && Arrays.equals(this.bytes, that.bytes);
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(bytes) + length;
    }

    public String toString(Charset charset) {
        return new String(bytes, 0, length, charset);
    }

    @Override
    public String toString() {
        return "Bytes[" +
                "bytes=" + Arrays.toString(bytes) + ", " +
                "length=" + length + ']';
    }

    public static Bytes copy(String str) {
        return new Bytes(str.getBytes(UTF_8));
    }

    public static Bytes copy(Bytes bytes) {
        return new Bytes(bytes.bytes.clone(), bytes.length());
    }

    public static Bytes copy(byte[] bytes) {
        return new Bytes(bytes.clone());
    }

    public static Bytes unsafeWrap(byte[] bytes) {
        return new Bytes(bytes);
    }

    public static Bytes unsafeWrap(byte[] bytes, int length) {
        return new Bytes(bytes, length);
    }
}
