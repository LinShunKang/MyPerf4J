package cn.myperf4j.base.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Created by LinShunkang on 2018/9/6
 */
public final class LineProtocolUtils {

    private static final Pattern SPACE_PATTERN = Pattern.compile(" ");

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private static final Pattern EQUAL_SIGN_PATTERN = Pattern.compile("=");

    private static final Map<String, String> methodNameMap = new ConcurrentHashMap<>(1024);


    /**
     * 用于把tagOrField里的 ','  ' '  '=' 转义为符合LineProtocol的格式
     *
     * @param tagOrField : tag key、tag value、field key
     * @return: 符合LineProtocol格式的文本
     */
    public static String processTagOrField(String tagOrField) {
        String lineProtocol = methodNameMap.get(tagOrField);
        if (lineProtocol != null) {
            return lineProtocol;
        }

        lineProtocol = SPACE_PATTERN.matcher(tagOrField).replaceAll("\\\\ ");
        lineProtocol = COMMA_PATTERN.matcher(lineProtocol).replaceAll("\\\\,");
        lineProtocol = EQUAL_SIGN_PATTERN.matcher(lineProtocol).replaceAll("\\\\=");
        methodNameMap.put(tagOrField, lineProtocol);
        return lineProtocol;
    }

}
