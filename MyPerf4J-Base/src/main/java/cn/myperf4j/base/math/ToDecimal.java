package cn.myperf4j.base.math;

/**
 * Created by LinShunkang on 2026/06/24
 * <p>
 * Note: This code was copied from {@link jdk.internal.math.ToDecimal} in JDK 25 and simplified.
 */
public abstract class ToDecimal {

    /* Used for left-to-tight digit extraction */
    static final int MASK_28 = (1 << 28) - 1;

    static final int NON_SPECIAL = 0 << 8;
    static final int PLUS_ZERO = 1 << 8;
    static final int MINUS_ZERO = 2 << 8;
    static final int PLUS_INF = 3 << 8;
    static final int MINUS_INF = 4 << 8;
    static final int NAN = 5 << 8;

    ToDecimal() {
    }

    static int putChar(byte[] str, int index, int c) {
        str[index] = (byte) c;
        return index + 1;
    }

    static int putDigit(byte[] str, int index, int d) {
        return putChar(str, index, (byte) ('0' + d));
    }

    static int put8Digits(byte[] str, int index, int m) {
        /*
         * Left-to-right digits extraction:
         * algorithm 1 in [3], with b = 10, k = 8, n = 28.
         */
        put8DigitsLatin1(str, index, m);
        return index + 8;
    }

    private static void put8DigitsLatin1(byte[] str, int index, int m) {
        int y = y(m);
        for (int i = 0; i < 8; ++i) {
            int t = 10 * y;
            str[index + i] = (byte) ('0' + (t >>> 28));
            y = t & MASK_28;
        }
    }

    static int y(int a) {
        /*
         * Algorithm 1 in [3] needs computation of
         *     floor((a + 1) 2^n / b^k) - 1
         * with a < 10^8, b = 10, k = 8, n = 28.
         * Noting that
         *     (a + 1) 2^n <= 10^8 2^28 < 10^17
         * For n = 17, m = 8 the table in section 10 of [1] leads to:
         */
        return (int) (multiplyHigh(
                (long) (a + 1) << 28,
                193_428_131_138_340_668L) >>> 20) - 1;
    }

    static long multiplyHigh(long x, long y) {
        // Use technique from section 8-2 of Henry S. Warren, Jr.,
        // Hacker's Delight (2nd ed.) (Addison Wesley, 2013), 173-174.
        long x1 = x >> 32;
        long x2 = x & 0xFFFFFFFFL;
        long y1 = y >> 32;
        long y2 = y & 0xFFFFFFFFL;

        long z2 = x2 * y2;
        long t = x1 * y2 + (z2 >>> 32);
        long z1 = t & 0xFFFFFFFFL;
        long z0 = t >> 32;
        z1 += x2 * y1;

        return x1 * y1 + z0 + (z1 >> 32);
    }

    static int removeTrailingZeroes(byte[] str, int index) {
        while (str[index - 1] == '0') {
            --index;
        }
        /* ... but do not remove the one directly to the right of '.' */
        if (str[index - 1] == '.') {
            ++index;
        }
        return index;
    }

    @SuppressWarnings("deprecation")
    static int putSpecial(byte[] str, int index, int type) {
        String s = special(type);
        int length = s.length();
        s.getBytes(0, length, str, index);
        return index + length;
    }

    static int length(byte[] str) {
        return str.length;
    }

    static String special(int type) {
        switch (type) {
            case PLUS_ZERO:
                return "0.0";
            case MINUS_ZERO:
                return "-0.0";
            case PLUS_INF:
                return "Infinity";
            case MINUS_INF:
                return "-Infinity";
            default:
                return "NaN";
        }
    }
}
