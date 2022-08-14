package cn.myperf4j.base.buffer;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2022/08/14
 */
public class LongBuffTest {

    @Test
    public void testLongBuf() {
        final LongBuf longBuf = new LongBuf(100);
        Assert.assertEquals(longBuf.capacity(), 100);

        longBuf.write(1L);
        longBuf.write(2L);
        Assert.assertEquals(longBuf.writerIndex(), 2L);

        longBuf.write(3);
        Assert.assertEquals(longBuf.writerIndex(), 3);

        Assert.assertEquals(longBuf.getLong(0), 1L);
        Assert.assertEquals(longBuf.getLong(1), 2L);
        Assert.assertEquals(longBuf.getLong(2), 3L);

        Assert.assertEquals(longBuf.writerIndex(), 3);
    }

    @Test
    public void testLongBufPool() {
        final LongBufPool pool = LongBufPool.getInstance();
        final LongBuf longBuf = pool.acquire(1024);
        Assert.assertEquals(longBuf.pool(), pool);

        longBuf.write(1);
        Assert.assertEquals(longBuf.writerIndex(), 1);

        pool.release(longBuf);
        Assert.assertEquals(longBuf.writerIndex(), 0);

        final LongBuf intBuf2 = pool.acquire(100 * 1024 + 1);
        Assert.assertNotEquals(intBuf2.pool(), pool);
        Assert.assertNull(intBuf2.pool());
    }
}
