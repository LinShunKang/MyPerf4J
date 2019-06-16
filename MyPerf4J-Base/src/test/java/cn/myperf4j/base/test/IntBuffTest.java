package cn.myperf4j.base.test;

import cn.myperf4j.base.buffer.IntBuf;
import cn.myperf4j.base.buffer.IntBufPool;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2019/06/16
 */
public class IntBuffTest {

    @Test
    public void testIntBuf() {
        IntBuf intBuf = new IntBuf(100);
        Assert.assertEquals(intBuf.capacity(), 100);

        intBuf.write(1);
        intBuf.write(2);
        Assert.assertEquals(intBuf.writerIndex(), 2);

        intBuf.write(3);
        Assert.assertEquals(intBuf.writerIndex(), 3);

        Assert.assertEquals(intBuf.getInt(0), 1);
        Assert.assertEquals(intBuf.getInt(1), 2);
        Assert.assertEquals(intBuf.getInt(2), 3);

        intBuf.setInt(0, 0);
        Assert.assertEquals(intBuf.getInt(0), 0);
        Assert.assertEquals(intBuf.writerIndex(), 3);
    }

    @Test
    public void testIntBufPool() {
        IntBufPool pool = IntBufPool.getInstance();
        IntBuf intBuf = pool.acquire(1024);
        Assert.assertEquals(intBuf.intBufPool(), pool);

        intBuf.write(1);
        Assert.assertEquals(intBuf.writerIndex(), 1);

        pool.release(intBuf);
        Assert.assertEquals(intBuf.writerIndex(), 0);

        IntBuf intBuf2 = pool.acquire(100 * 1024 + 1);
        Assert.assertNotEquals(intBuf2.intBufPool(), pool);
        Assert.assertNull(intBuf2.intBufPool());
    }

}
