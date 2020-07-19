package cn.myperf4j.base.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by LinShunkang on 2019/05/12
 */
public final class StrUtils {

    private static final String[] BYTE2HEX_PAD = new String[256];
    private static final String[] BYTE2HEX_NOPAD = new String[256];
    private static final byte[] HEX2B;

    static {
        // Generate the lookup table that converts a byte into a 2-digit hexadecimal integer.
        for (int i = 0; i < BYTE2HEX_PAD.length; i++) {
            String str = Integer.toHexString(i);
            BYTE2HEX_PAD[i] = i > 0xf ? str : ('0' + str);
            BYTE2HEX_NOPAD[i] = str;
        }
        // Generate the lookup table that converts an hex char into its decimal value:
        // the size of the table is such that the JVM is capable of save any bounds-check
        // if a char type is used as an index.
        HEX2B = new byte[Character.MAX_VALUE + 1];
        Arrays.fill(HEX2B, (byte) -1);
        HEX2B['0'] = (byte) 0;
        HEX2B['1'] = (byte) 1;
        HEX2B['2'] = (byte) 2;
        HEX2B['3'] = (byte) 3;
        HEX2B['4'] = (byte) 4;
        HEX2B['5'] = (byte) 5;
        HEX2B['6'] = (byte) 6;
        HEX2B['7'] = (byte) 7;
        HEX2B['8'] = (byte) 8;
        HEX2B['9'] = (byte) 9;
        HEX2B['A'] = (byte) 10;
        HEX2B['B'] = (byte) 11;
        HEX2B['C'] = (byte) 12;
        HEX2B['D'] = (byte) 13;
        HEX2B['E'] = (byte) 14;
        HEX2B['F'] = (byte) 15;
        HEX2B['a'] = (byte) 10;
        HEX2B['b'] = (byte) 11;
        HEX2B['c'] = (byte) 12;
        HEX2B['d'] = (byte) 13;
        HEX2B['e'] = (byte) 14;
        HEX2B['f'] = (byte) 15;
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isBlank(String str) {
        if (str == null || str.isEmpty()) {
            return true;
        }

        for (int i = 0; i < str.length(); i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(String str) {
        return !isBlank(str);
    }

    public static List<String> splitAsList(final String str, final char separatorChar) {
        if (str == null || str.isEmpty()) {
            return Collections.emptyList();
        }

        List<String> result = new ArrayList<>();
        splitWorker(str, separatorChar, false, result);
        return result;
    }

    private static void splitWorker(final String str,
                                    final char separatorChar,
                                    final boolean preserveAllTokens,
                                    final List<String> list) {
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        while (i < str.length()) {
            if (str.charAt(i) == separatorChar) {
                if (match || preserveAllTokens) {
                    list.add(str.substring(start, i));
                    match = false;
                    lastMatch = true;
                }
                start = ++i;
                continue;
            }
            lastMatch = false;
            match = true;
            i++;
        }

        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
    }

    /**
     * Decode a 2-digit hex byte from within a string.
     */
    public static byte decodeHexByte(CharSequence s, int pos) {
        int hi = decodeHexNibble(s.charAt(pos));
        int lo = decodeHexNibble(s.charAt(pos + 1));
        if (hi == -1 || lo == -1) {
            throw new IllegalArgumentException(String.format(
                    "invalid hex byte '%s' at index %d of '%s'", s.subSequence(pos, pos + 2), pos, s));
        }
        return (byte) ((hi << 4) + lo);
    }

    public static int decodeHexNibble(final char c) {
        assert HEX2B.length == (Character.MAX_VALUE + 1);
        // Character.digit() is not used here, as it addresses a larger
        // set of characters (both ASCII and full-width latin letters).
        return HEX2B[c];
    }

}
