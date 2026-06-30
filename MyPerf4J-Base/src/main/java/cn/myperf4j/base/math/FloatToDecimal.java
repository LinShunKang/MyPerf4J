package cn.myperf4j.base.math;

import java.io.IOException;

import static cn.myperf4j.base.math.MathUtils.flog10pow2;
import static cn.myperf4j.base.math.MathUtils.flog10threeQuartersPow2;
import static cn.myperf4j.base.math.MathUtils.flog2pow10;
import static cn.myperf4j.base.math.MathUtils.g1;
import static cn.myperf4j.base.math.MathUtils.pow10;
import static java.lang.Float.floatToRawIntBits;
import static java.lang.Integer.numberOfLeadingZeros;

/**
 * Created by LinShunkang on 2026/06/24
 * <p>
 * This class exposes a method to render a {@code float} as a string.
 * Note: This code was copied from {@link jdk.internal.math.FloatToDecimal} in JDK 25 and simplified.
 */
public final class FloatToDecimal extends ToDecimal {

    /* The precision in bits */
    static final int P = 24;

    /* Exponent width in bits */
    private static final int W = (Float.SIZE - 1) - (P - 1);

    /* Minimum value of the exponent: -(2^(W-1)) - P + 3 */
    static final int Q_MIN = (-1 << (W - 1)) - P + 3;

    /* Threshold to detect tiny values, as in section 8.2.1 of [1] */
    static final int C_TINY = 8;

    /* H is as in section 8.1 of [1] */
    static final int H = 9;

    /* Minimum value of the significand of a normal value: 2^(P-1) */
    private static final int C_MIN = 1 << (P - 1);

    /* Mask to extract the biased exponent */
    private static final int BQ_MASK = (1 << W) - 1;

    /* Mask to extract the fraction bits */
    private static final int T_MASK = (1 << (P - 1)) - 1;

    /* Used in rop() */
    private static final long MASK_32 = (1L << 32) - 1;

    /*
     * Room for the longer of the forms
     *     -ddddd.dddd         H + 2 characters
     *     -0.00ddddddddd      H + 5 characters
     *     -d.ddddddddE-ee     H + 6 characters
     * where there are H digits d
     */
    public static final int MAX_CHARS = H + 6;

    private FloatToDecimal() {
    }

    /**
     * Appends the rendering of the {@code v} to {@code str}.
     *
     * <p>The outcome is the same as if {@code v} were first
     * {@link #toString(double) rendered} and the resulting string were then
     *
     * @param str   the String byte array to append to
     * @param index the index into str
     * @param v     the {@code float} whose rendering is into str.
     * @throws IOException If an I/O error occurs
     */
    public static int putDecimal(byte[] str, int index, float v) {
        assert 0 <= index && index <= length(str) - MAX_CHARS : "Trusted caller missed bounds check";

        int pair = toDecimal(str, index, v);
        int type = pair & 0xFF00;
        if (type == NON_SPECIAL) {
            return index + (pair & 0xFF);
        }
        return putSpecial(str, index, type);
    }

    /*
     * Returns
     *     Combine type and size, the first byte is size, the second byte is type
     *
     *     PLUS_ZERO       iff v is 0.0
     *     MINUS_ZERO      iff v is -0.0
     *     PLUS_INF        iff v is POSITIVE_INFINITY
     *     MINUS_INF       iff v is NEGATIVE_INFINITY
     *     NAN             iff v is NaN
     */
    private static int toDecimal(byte[] str, int index, float v) {
        /*
         * For full details see references [2] and [1].
         *
         * For finite v != 0, determine integers c and q such that
         *     |v| = c 2^q    and
         *     Q_MIN <= q <= Q_MAX    and
         *         either    2^(P-1) <= c < 2^P                 (normal)
         *         or        0 < c < 2^(P-1)  and  q = Q_MIN    (subnormal)
         */
        int bits = floatToRawIntBits(v);
        int t = bits & T_MASK;
        int bq = (bits >>> P - 1) & BQ_MASK;
        if (bq < BQ_MASK) {
            int start = index;
            if (bits < 0) {
                index = putChar(str, index, '-');
            }
            if (bq != 0) {
                /* normal value. Here mq = -q */
                int mq = -Q_MIN + 1 - bq;
                int c = C_MIN | t;
                /* The fast path discussed in section 8.3 of [1] */
                if (0 < mq & mq < P) {
                    int f = c >> mq;
                    if (f << mq == c) {
                        return toChars(str, index, f, 0) - start;
                    }
                }
                return toDecimal(str, index, -mq, c, 0) - start;
            }
            if (t != 0) {
                /* subnormal value */
                return (t < C_TINY
                        ? toDecimal(str, index, Q_MIN, 10 * t, -1)
                        : toDecimal(str, index, Q_MIN, t, 0)) - start;
            }
            return bits == 0 ? PLUS_ZERO : MINUS_ZERO;
        }
        if (t != 0) {
            return NAN;
        }
        return bits > 0 ? PLUS_INF : MINUS_INF;
    }

    private static int toDecimal(byte[] str, int index, int q, int c, int dk) {
        /*
         * The skeleton corresponds to figure 7 of [1].
         * The efficient computations are those summarized in figure 9.
         * Also check the appendix.
         *
         * Here's a correspondence between Java names and names in [1],
         * expressed as approximate LaTeX source code and informally.
         * Other names are identical.
         * cb:     \bar{c}     "c-bar"
         * cbr:    \bar{c}_r   "c-bar-r"
         * cbl:    \bar{c}_l   "c-bar-l"
         *
         * vb:     \bar{v}     "v-bar"
         * vbr:    \bar{v}_r   "v-bar-r"
         * vbl:    \bar{v}_l   "v-bar-l"
         *
         * rop:    r_o'        "r-o-prime"
         */
        int out = c & 0x1;
        long cb = c << 2;
        long cbr = cb + 2;
        long cbl;
        int k;
        /*
         * flog10pow2(e) = floor(log_10(2^e))
         * flog10threeQuartersPow2(e) = floor(log_10(3/4 2^e))
         * flog2pow10(e) = floor(log_2(10^e))
         */
        if (c != C_MIN | q == Q_MIN) {
            /* regular spacing */
            cbl = cb - 2;
            k = flog10pow2(q);
        } else {
            /* irregular spacing */
            cbl = cb - 1;
            k = flog10threeQuartersPow2(q);
        }
        int h = q + flog2pow10(-k) + 33;

        /* g is as in the appendix */
        long g = g1(k) + 1;

        int vb = rop(g, cb << h);
        int vbl = rop(g, cbl << h);
        int vbr = rop(g, cbr << h);

        int s = vb >> 2;
        if (s >= 100) {
            /*
             * For n = 9, m = 1 the table in section 10 of [1] shows
             *     s' = floor(s / 10) = floor(s 1_717_986_919 / 2^34)
             *
             * sp10 = 10 s'
             * tp10 = 10 t'
             * upin    iff    u' = sp10 10^k in Rv
             * wpin    iff    w' = tp10 10^k in Rv
             * See section 9.3 of [1].
             */
            int sp10 = 10 * (int) (s * 1_717_986_919L >>> 34);
            int tp10 = sp10 + 10;
            boolean upin = vbl + out <= sp10 << 2;
            boolean wpin = (tp10 << 2) + out <= vbr;
            if (upin != wpin) {
                return toChars(str, index, upin ? sp10 : tp10, k);
            }
        }

        /*
         * 10 <= s < 100    or    s >= 100  and  u', w' not in Rv
         * uin    iff    u = s 10^k in Rv
         * win    iff    w = t 10^k in Rv
         * See section 9.3 of [1].
         */
        int t = s + 1;
        boolean uin = vbl + out <= s << 2;
        boolean win = (t << 2) + out <= vbr;
        if (uin != win) {
            /* Exactly one of u or w lies in Rv */
            return toChars(str, index, uin ? s : t, k + dk);
        }
        /*
         * Both u and w lie in Rv: determine the one closest to v.
         * See section 9.3 of [1].
         */
        int cmp = vb - (s + t << 1);
        return toChars(str, index, cmp < 0 || cmp == 0 && (s & 0x1) == 0 ? s : t, k + dk);
    }

    /*
     * Computes rop(cp g 2^(-95))
     * See appendix and figure 11 of [1].
     */
    private static int rop(long g, long cp) {
        long x1 = multiplyHigh(g, cp);
        long vbp = x1 >>> 31;
        return (int) (vbp | (x1 & MASK_32) + MASK_32 >>> 32);
    }

    /*
     * Formats the decimal f 10^e.
     */
    private static int toChars(byte[] str, int index, int f, int e) {
        /*
         * For details not discussed here see section 10 of [1].
         *
         * Determine len such that
         *     10^(len-1) <= f < 10^len
         */
        int len = flog10pow2(Integer.SIZE - numberOfLeadingZeros(f));
        if (f >= pow10(len)) {
            len += 1;
        }

        /*
         * Let fp and ep be the original f and e, respectively.
         * Transform f and e to ensure
         *     10^(H-1) <= f < 10^H
         *     fp 10^ep = f 10^(e-H) = 0.f 10^e
         */
        f *= (int) pow10(H - len);
        e += len;

        /*
         * The toChars?() methods perform left-to-right digits extraction
         * using ints, provided that the arguments are limited to 8 digits.
         * Therefore, split the H = 9 digits of f into:
         *     h = the most significant digit of f
         *     l = the last 8, least significant digits of f
         *
         * For n = 9, m = 8 the table in section 10 of [1] shows
         *     floor(f / 10^8) = floor(1_441_151_881 f / 2^57)
         */
        int h = (int) (f * 1_441_151_881L >>> 57);
        int l = f - 100_000_000 * h;

        if (0 < e && e <= 7) {
            return toChars1(str, index, h, l, e);
        }
        if (-3 < e && e <= 0) {
            return toChars2(str, index, h, l, e);
        }
        return toChars3(str, index, h, l, e);
    }

    private static int toChars1(byte[] str, int index, int h, int l, int e) {
        /*
         * 0 < e <= 7: plain format without leading zeroes.
         * Left-to-right digits extraction:
         * algorithm 1 in [3], with b = 10, k = 8, n = 28.
         */
        index = putDigit(str, index, h);
        int y = y(l);
        int t;
        int i = 1;
        for (; i < e; ++i) {
            t = 10 * y;
            index = putDigit(str, index, t >>> 28);
            y = t & MASK_28;
        }
        index = putChar(str, index, '.');
        for (; i <= 8; ++i) {
            t = 10 * y;
            index = putDigit(str, index, t >>> 28);
            y = t & MASK_28;
        }
        return removeTrailingZeroes(str, index);
    }

    private static int toChars2(byte[] str, int index, int h, int l, int e) {
        /* -3 < e <= 0: plain format with leading zeroes */
        index = putDigit(str, index, 0);
        index = putChar(str, index, '.');
        for (; e < 0; ++e) {
            index = putDigit(str, index, 0);
        }
        index = putDigit(str, index, h);
        index = put8Digits(str, index, l);
        return removeTrailingZeroes(str, index);
    }

    private static int toChars3(byte[] str, int index, int h, int l, int e) {
        /* -3 >= e | e > 7: computerized scientific notation */
        index = putDigit(str, index, h);
        index = putChar(str, index, '.');
        index = put8Digits(str, index, l);
        index = removeTrailingZeroes(str, index);
        return exponent(str, index, e - 1);
    }

    private static int exponent(byte[] str, int index, int e) {
        index = putChar(str, index, 'E');
        if (e < 0) {
            index = putChar(str, index, '-');
            e = -e;
        }
        if (e < 10) {
            return putDigit(str, index, e);
        }
        /*
         * For n = 2, m = 1 the table in section 10 of [1] shows
         *     floor(e / 10) = floor(103 e / 2^10)
         */
        int d = e * 103 >>> 10;
        index = putDigit(str, index, d);
        return putDigit(str, index, e - 10 * d);
    }
}
