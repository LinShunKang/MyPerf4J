package cn.myperf4j.base.buffer;

/**
 * Created by LinShunkang on 2019/06/12
 */
public class IntBuf {

    private final int[] buf;

    private int writerIndex;

    private final IntBufPool pool;

    public IntBuf(int capacity, IntBufPool pool) {
        this.buf = new int[capacity];
        this.writerIndex = 0;
        this.pool = pool;
    }

    public IntBuf(int capacity) {
        this.buf = new int[capacity];
        this.writerIndex = 0;
        this.pool = null;
    }

    public void write(int value) {
        ensureWritable(1);
        this.buf[writerIndex++] = value;
    }

    private void ensureWritable(int minWritableSize) {
        if (minWritableSize > buf.length - writerIndex) {
            throw new IndexOutOfBoundsException("IntBuf minWritableSize(" + minWritableSize + ") + writerIndex(" + writerIndex + ") exceed buf.length(" + buf.length + ")");
        }
    }

    public void setInt(int index, int value) {
        checkBounds(index);
        this.buf[index] = value;
    }

    private void checkBounds(int index) {
        if (index >= buf.length) {
            throw new IndexOutOfBoundsException("IntBuf index(" + index + ") exceed buf.length(" + buf.length + ")");
        }
    }

    public int capacity() {
        return buf.length;
    }

    public int writerIndex() {
        return writerIndex;
    }

    public void writerIndex(int writerIndex) {
        this.writerIndex = writerIndex;
    }

    public int getInt(int index) {
        checkBounds(index);
        return buf[index];
    }

    public int[] _buf() {
        return buf;
    }

    public void reset() {
        this.writerIndex = 0;
    }

    public IntBufPool intBufPool() {
        return pool;
    }
}
