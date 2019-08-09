package cn.myperf4j.base.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by LinShunkang on 2019/05/12
 */
public final class StrUtils {

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
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
}
