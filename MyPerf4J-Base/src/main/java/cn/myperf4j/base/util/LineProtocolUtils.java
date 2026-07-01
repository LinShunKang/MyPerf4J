package cn.myperf4j.base.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/9/6
 */
public final class LineProtocolUtils {

    private static final Map<String, String> methodNameMap = new ConcurrentHashMap<>(1024);

    private LineProtocolUtils() {
        //empty
    }

    /**
     * 用于把tagOrField里的 ','、' ' 和 '=' 转义为符合LineProtocol的格式
     *
     * @param tagOrField : tag key、tag value、field key
     * @return 符合 LineProtocol 格式的文本
     */
    public static String processTagOrField(String tagOrField) {
        final String cached = methodNameMap.get(tagOrField); //Fast Path
        if (cached != null) {
            return cached;
        }
        return methodNameMap.computeIfAbsent(tagOrField, LineProtocolUtils::escapeTagOrField);
    }

    private static String escapeTagOrField(String str) {
        final int index = findEscapeIndex(str);
        if (index == -1) {
            return str;
        }

        final StringBuilder sb = new StringBuilder(str.length() + 8).append(str, 0, index);
        for (int i = index, size = str.length(); i < size; i++) {
            final char ch = str.charAt(i);
            if (isEscapeCh(ch)) {
                sb.append('\\');
            }
            sb.append(ch);
        }
        return sb.toString();
    }

    private static int findEscapeIndex(String str) {
        for (int i = 0, size = str.length(); i < size; i++) {
            if (isEscapeCh(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private static boolean isEscapeCh(char ch) {
        return ch == ' ' || ch == ',' || ch == '=';
    }
}
