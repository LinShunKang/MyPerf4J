package cn.myperf4j.base.util;

/**
 * Created by LinShunkang on 2026/06/21
 * <p>
 * Note: This code was copied from {@link jdk.internal.util.DecimalDigits} in JDK 25.
 */
public final class DigitUtils {

    /**
     * Each element of the array represents the packaging of two ascii characters based on little endian:<p>
     * <pre>
     *      00 -> '0' | ('0' << 8) -> 0x3030
     *      01 -> '0' | ('1' << 8) -> 0x3130
     *      02 -> '0' | ('2' << 8) -> 0x3230
     *
     *     ...
     *
     *      10 -> '1' | ('0' << 8) -> 0x3031
     *      11 -> '1' | ('1' << 8) -> 0x3131
     *      12 -> '1' | ('2' << 8) -> 0x3231
     *
     *     ...
     *
     *      97 -> '9' | ('7' << 8) -> 0x3739
     *      98 -> '9' | ('8' << 8) -> 0x3839
     *      99 -> '9' | ('9' << 8) -> 0x3939
     * </pre>
     */
    private static final short[] DIGITS;

    static {
        final short[] digits = new short[128];
        for (int i = 0; i < 10; i++) {
            final short hi = (short) (i + '0');
            for (int j = 0; j < 10; j++) {
                final short lo = (short) ((j + '0') << 8);
                digits[i * 10 + j] = (short) (hi | lo);
            }
        }
        DIGITS = digits;
    }

    /**
     * Places characters representing the integer i into the
     * character array buf. The characters are placed into
     * the buffer backwards starting with the least significant
     * digit at the specified index (exclusive), and working
     * backwards from there.
     *
     * @param i     value to convert
     * @param index next index, after the least significant digit
     * @param buf   target buffer, Latin1-encoded
     * @return index of the most significant digit or minus sign, if present
     * @implNote This method converts positive inputs into negative
     * values, to cover the Integer.MIN_VALUE case. Converting otherwise
     * (negative to positive) will expose -Integer.MIN_VALUE that overflows
     * integer.
     * <p>
     * <b>WARNING: This method does not perform any bound checks. </b>
     */
    public static int uncheckedGetCharsLatin1(int i, int index, byte[] buf) {
        int q;
        int charPos = index;

        boolean negative = i < 0;
        if (!negative) {
            i = -i;
        }

        // Generate two digits per iteration
        while (i <= -100) {
            q = i / 100;
            charPos -= 2;
            uncheckedPutPairLatin1(buf, charPos, (q * 100) - i);
            i = q;
        }

        // We know there are at most two digits left at this point.
        if (i <= -10) {
            charPos -= 2;
            uncheckedPutPairLatin1(buf, charPos, -i);
        } else {
            uncheckedPutCharLatin1(buf, --charPos, '0' - i);
        }

        if (negative) {
            uncheckedPutCharLatin1(buf, --charPos, '-');
        }
        return charPos;
    }

    /**
     * Insert the 2-bytes integer into the buf as 2 decimal digit ASCII bytes,
     * only least significant 16 bits of {@code v} are used.
     * <p>
     * <b>WARNING: This method does not perform any bound checks.</b>
     *
     * @param buf     byte buffer to copy into
     * @param charPos insert point
     * @param v       to convert
     */
    public static void uncheckedPutPairLatin1(byte[] buf, int charPos, int v) {
        final int packed = DIGITS[v & 0x7f];
        uncheckedPutCharLatin1(buf, charPos, packed & 0xFF);
        uncheckedPutCharLatin1(buf, charPos + 1, packed >> 8);
    }

    private static void uncheckedPutCharLatin1(byte[] buf, int charPos, int c) {
        assert charPos >= 0 && charPos < buf.length;
        buf[charPos] = (byte) c;
    }

    /**
     * Places characters representing the long i into the
     * character array buf. The characters are placed into
     * the buffer backwards starting with the least significant
     * digit at the specified index (exclusive), and working
     * backwards from there.
     *
     * @param i     value to convert
     * @param index next index, after the least significant digit
     * @param buf   target buffer, Latin1-encoded
     * @return index of the most significant digit or minus sign, if present
     * @implNote This method converts positive inputs into negative
     * values, to cover the Long.MIN_VALUE case. Converting otherwise
     * (negative to positive) will expose -Long.MIN_VALUE that overflows
     * long.
     * <p>
     * <b>WARNING: This method does not perform any bound checks. </b>
     */
    public static int uncheckedGetCharsLatin1(long i, int index, byte[] buf) {
        long q;
        int charPos = index;

        boolean negative = i < 0;
        if (!negative) {
            i = -i;
        }

        // Get 2 digits/iteration using longs until quotient fits into an int
        while (i < Integer.MIN_VALUE) {
            q = i / 100;
            charPos -= 2;
            uncheckedPutPairLatin1(buf, charPos, (int) ((q * 100) - i));
            i = q;
        }

        // Get 2 digits/iteration using ints
        int q2;
        int i2 = (int) i;
        while (i2 <= -100) {
            q2 = i2 / 100;
            charPos -= 2;
            uncheckedPutPairLatin1(buf, charPos, (q2 * 100) - i2);
            i2 = q2;
        }

        // We know there are at most two digits left at this point.
        if (i2 <= -10) {
            charPos -= 2;
            uncheckedPutPairLatin1(buf, charPos, -i2);
        } else {
            uncheckedPutCharLatin1(buf, --charPos, '0' - i2);
        }

        if (negative) {
            uncheckedPutCharLatin1(buf, --charPos, '-');
        }
        return charPos;
    }

    public static int stringSize(int x) {
        int d = 1;
        if (x >= 0) {
            d = 0;
            x = -x;
        }

        int p = -10;
        for (int i = 1; i < 10; i++) {
            if (x > p) {
                return i + d;
            }
            p = 10 * p;
        }
        return 10 + d;
    }

    public static int stringSize(long x) {
        int d = 1;
        if (x >= 0) {
            d = 0;
            x = -x;
        }

        long p = -10;
        for (int i = 1; i < 19; i++) {
            if (x > p) {
                return i + d;
            }
            p = 10 * p;
        }
        return 19 + d;
    }

    private DigitUtils() {
    }
}
