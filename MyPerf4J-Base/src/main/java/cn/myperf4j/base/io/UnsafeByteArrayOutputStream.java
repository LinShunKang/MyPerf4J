package cn.myperf4j.base.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static cn.myperf4j.base.io.Bytes.unsafeWrap;

/**
 * Created by LinShunkang on 2026/06/19
 */
public class UnsafeByteArrayOutputStream extends ByteArrayOutputStream {

    public UnsafeByteArrayOutputStream() {
        this(128);
    }

    public UnsafeByteArrayOutputStream(int size) {
        super(size);
    }

    public Bytes toBytes() {
        return unsafeWrap(this.buf, this.count);
    }

    @Override
    public void reset() {
        this.count = 0;
    }

    @Override
    public void close() throws IOException {
        this.count = 0;
    }
}
