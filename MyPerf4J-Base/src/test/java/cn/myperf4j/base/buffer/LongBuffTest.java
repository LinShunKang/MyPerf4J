package cn.myperf4j.base.buffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Created by LinShunkang on 2022/08/14
 */
public class LongBuffTest {

    @Test
    public void testLongBuf() {
        try (LongBuf longBuf = new LongBuf(100)) {
            Assertions.assertEquals(100, longBuf.capacity());

            longBuf.write(1L);
            longBuf.write(2L);
            Assertions.assertEquals(2L, longBuf.writerIndex());

            longBuf.write(3);
            Assertions.assertEquals(3, longBuf.writerIndex());

            Assertions.assertEquals(1L, longBuf.getLong(0));
            Assertions.assertEquals(2L, longBuf.getLong(1));
            Assertions.assertEquals(3L, longBuf.getLong(2));

            Assertions.assertEquals(3, longBuf.writerIndex());
        }
    }

    @Test
    public void testLongBufPool() {
        final LongBufPool pool = LongBufPool.getInstance();
        try (LongBuf longBuf = pool.acquire(1024)) {
            Assertions.assertEquals(longBuf.pool(), pool);
            longBuf.write(1);
            Assertions.assertEquals(1, longBuf.writerIndex());

            longBuf.reset();
            Assertions.assertEquals(0, longBuf.writerIndex());
        }

        try (LongBuf intBuf2 = pool.acquire(100 * 1024 + 1)) {
            Assertions.assertNotEquals(intBuf2.pool(), pool);
            Assertions.assertNull(intBuf2.pool());
        }
    }
}
