package cn.myperf4j.bench.util.concurrent;

import java.util.concurrent.atomic.AtomicLong;

public final class SimpleRandom {

    private static final long multiplier = 0x5DEECE66DL;

    private static final long addend = 0xBL;

    private static final long mask = (1L << 48) - 1;

    static final AtomicLong seq = new AtomicLong(-715159705);

    private long seed;

    public SimpleRandom() {
        seed = System.nanoTime() + seq.getAndAdd(129);
    }

    public int next() {
        return (int) ((seed = (seed * multiplier + addend) & mask) >>> 17) & 0x7FFFFFFF;
    }
}
