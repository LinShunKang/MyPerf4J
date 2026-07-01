package cn.myperf4j.base.io;

import cn.myperf4j.base.math.DoubleToDecimal;
import cn.myperf4j.base.math.FloatToDecimal;
import cn.myperf4j.base.util.DigitUtils;

import java.util.Arrays;

import static cn.myperf4j.base.io.Bytes.unsafeWrap;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/06/19
 */
public final class BytesBuilder implements AutoCloseable {

    private static final int SOFT_MAX_ARRAY_LENGTH = Integer.MAX_VALUE - 8;

    private byte[] value;

    private int count;

    public BytesBuilder(int capacity) {
        this.value = new byte[capacity];
        this.count = 0;
    }

    public BytesBuilder append(byte b) {
        ensureCapacity(this.count + 1);
        this.value[this.count++] = b;
        return this;
    }

    private void ensureCapacity(int minCapacity) {
        // overflow-conscious code
        final int oldCapacity = this.value.length;
        final int minGrowth = minCapacity - oldCapacity;
        if (minGrowth > 0) {
            this.value = Arrays.copyOf(this.value, newLength(oldCapacity, minGrowth, oldCapacity));
        }
    }

    private static int newLength(int oldLength, int minGrowth, int prefGrowth) {
        final int prefLength = oldLength + Math.max(minGrowth, prefGrowth); // might overflow
        if (0 < prefLength && prefLength <= SOFT_MAX_ARRAY_LENGTH) {
            return prefLength;
        }
        return hugeLength(oldLength, minGrowth); // put code cold in a separate method
    }

    private static int hugeLength(int oldLength, int minGrowth) {
        final int minLength = oldLength + minGrowth;
        if (minLength < 0) { // overflow
            throw new OutOfMemoryError("Required array length " + oldLength + " + " + minGrowth + " is too large");
        }
        return Math.max(minLength, SOFT_MAX_ARRAY_LENGTH);
    }

    public BytesBuilder append(int i) {
        final int spaceNeeded = this.count + DigitUtils.stringSize(i);
        ensureCapacity(spaceNeeded);
        DigitUtils.uncheckedGetCharsLatin1(i, spaceNeeded, this.value);
        this.count = spaceNeeded;
        return this;
    }

    public BytesBuilder append(long l) {
        final int spaceNeeded = this.count + DigitUtils.stringSize(l);
        ensureCapacity(spaceNeeded);
        DigitUtils.uncheckedGetCharsLatin1(l, spaceNeeded, this.value);
        this.count = spaceNeeded;
        return this;
    }

    public BytesBuilder append(float f) {
        final int spaceNeeded = this.count + FloatToDecimal.MAX_CHARS;
        ensureCapacity(spaceNeeded);
        this.count = FloatToDecimal.putDecimal(this.value, this.count, f);
        return this;
    }

    public BytesBuilder append(double d) {
        final int spaceNeeded = this.count + DoubleToDecimal.MAX_CHARS;
        ensureCapacity(spaceNeeded);
        this.count = DoubleToDecimal.putDecimal(this.value, this.count, d);
        return this;
    }

    public BytesBuilder append(char c) {
        final int spaceNeeded = this.count + (c < 0x80 ? 1 : (c < 0x800 ? 2 : 3));
        ensureCapacity(spaceNeeded);
        if (c < 0x80) {
            this.value[this.count++] = (byte) c;
        } else if (c < 0x800) {
            this.value[this.count++] = (byte) (0xC0 | (c >> 6));
            this.value[this.count++] = (byte) (0x80 | (c & 0x3F));
        } else if (Character.isSurrogate(c)) {
            this.value[this.count++] = (byte) '?';
        } else {
            this.value[this.count++] = (byte) (0xE0 | (c >> 12));
            this.value[this.count++] = (byte) (0x80 | ((c >> 6) & 0x3F));
            this.value[this.count++] = (byte) (0x80 | (c & 0x3F));
        }
        return this;
    }

    public BytesBuilder append(String value) {
        return append(value.getBytes(UTF_8));
    }

    public BytesBuilder append(byte[] value) {
        final int spaceNeeded = this.count + value.length;
        ensureCapacity(spaceNeeded);
        System.arraycopy(value, 0, this.value, this.count, value.length);
        this.count = spaceNeeded;
        return this;
    }

    public int getCount() {
        return this.count;
    }

    public Bytes toBytes() {
        return unsafeWrap(this.value, this.count);
    }

    public Bytes toBytes(int count) {
        return unsafeWrap(this.value, count);
    }

    @Override
    public String toString() {
        return new String(this.value, 0, this.count, UTF_8);
    }

    @Override
    public void close() {
        this.count = 0;
    }
}
