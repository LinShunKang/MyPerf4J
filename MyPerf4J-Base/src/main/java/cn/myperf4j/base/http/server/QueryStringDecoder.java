package cn.myperf4j.base.http.server;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static cn.myperf4j.base.util.StrUtils.decodeHexByte;

/**
 * Created by LinShunkang on 2020/07/12
 */
public class QueryStringDecoder {

    public static final String EMPTY_STRING = "";

    public static final char SPACE = 0x20;

    private static final int DEFAULT_MAX_PARAMS = 1024;

    private static final ThreadLocal<TempBuf> DECODE_TEMP_BUF = new ThreadLocal<TempBuf>() {
        @Override
        protected TempBuf initialValue() {
            return new TempBuf(1024);
        }
    };

    private final Charset charset;
    private final String uri;
    private final int maxParams;
    private final boolean semicolonIsNormalChar;
    private int pathEndIdx;
    private Map<String, List<String>> params;

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath) {
        this(uri, charset, hasPath, DEFAULT_MAX_PARAMS);
    }

    public QueryStringDecoder(String uri, Charset charset, boolean hasPath, int maxParams) {
        //pathEndIdx `-1` means that path end index will be initialized lazily
        this(uri, charset, hasPath ? -1 : 0, maxParams, false);
    }

    public QueryStringDecoder(String uri,
                              Charset charset,
                              int pathEndIdx,
                              int maxParams,
                              boolean semicolonIsNormalChar) {
        if (uri == null || charset == null || maxParams <= 0) {
            throw new IllegalArgumentException("uri == null || charset == null || maxParams <= 0");
        }
        this.uri = uri;
        this.charset = charset;
        this.maxParams = maxParams;
        this.pathEndIdx = pathEndIdx;
        this.semicolonIsNormalChar = semicolonIsNormalChar;
    }

    public Map<String, List<String>> parameters() {
        if (params == null) {
            params = decodeParams(uri, pathEndIdx(), charset, maxParams, semicolonIsNormalChar);
        }
        return params;
    }

    private int pathEndIdx() {
        if (pathEndIdx == -1) {
            pathEndIdx = findPathEndIndex(uri);
        }
        return pathEndIdx;
    }

    private static Map<String, List<String>> decodeParams(String str,
                                                          int from,
                                                          Charset charset,
                                                          int paramsLimit,
                                                          boolean semicolonIsNormalChar) {
        int len = str.length();
        if (from >= len) {
            return Collections.emptyMap();
        }

        if (str.charAt(from) == '?') {
            from++;
        }

        TempBuf tempBuf = DECODE_TEMP_BUF.get();
        Map<String, List<String>> params = new LinkedHashMap<>();
        int nameStart = from;
        int valueStart = -1;
        int i;
        loop:
        for (i = from; i < len; i++) {
            switch (str.charAt(i)) {
                case '=':
                    if (nameStart == i) {
                        nameStart = i + 1;
                    } else if (valueStart < nameStart) {
                        valueStart = i + 1;
                    }
                    break;
                case ';':
                    if (semicolonIsNormalChar) {
                        continue;
                    }
                    // fall-through
                case '&':
                    if (addParam(str, nameStart, valueStart, i, params, charset, tempBuf)) {
                        paramsLimit--;
                        if (paramsLimit == 0) {
                            return params;
                        }
                    }
                    nameStart = i + 1;
                    break;
                case '#':
                    break loop;
                default:
                    // continue
            }
        }
        addParam(str, nameStart, valueStart, i, params, charset, tempBuf);
        return params;
    }

    private static boolean addParam(String s,
                                    int nameStart,
                                    int valueStart,
                                    int valueEnd,
                                    Map<String, List<String>> params,
                                    Charset charset,
                                    TempBuf tempBuf) {
        if (nameStart >= valueEnd) {
            return false;
        }

        if (valueStart <= nameStart) {
            valueStart = valueEnd + 1;
        }

        String name = decodeComponent(s, nameStart, valueStart - 1, charset, false, tempBuf);
        String value = decodeComponent(s, valueStart, valueEnd, charset, false, tempBuf);
        List<String> values = params.get(name);
        if (values == null) {
            values = new ArrayList<>(1);  // Often there's only 1 value.
            params.put(name, values);
        }
        values.add(value);
        return true;
    }

    private static String decodeComponent(String s,
                                          int from,
                                          int toExcluded,
                                          Charset charset,
                                          boolean isPath,
                                          TempBuf tempBuf) {
        int len = toExcluded - from;
        if (len <= 0) {
            return EMPTY_STRING;
        }

        int firstEscaped = -1;
        for (int i = from; i < toExcluded; i++) {
            char c = s.charAt(i);
            if (c == '%' || c == '+' && !isPath) {
                firstEscaped = i;
                break;
            }
        }

        if (firstEscaped == -1) {
            return s.substring(from, toExcluded);
        }

        // Each encoded byte takes 3 characters (e.g. "%20")
        int decodedCapacity = (toExcluded - firstEscaped) / 3;
        byte[] buf = tempBuf.byteBuf(decodedCapacity);
        char[] charBuf = tempBuf.charBuf(len);
        s.getChars(from, firstEscaped, charBuf, 0);

        int charBufIdx = firstEscaped - from;
        return decodeComponent(s, firstEscaped, toExcluded, charset, isPath, buf, charBuf, charBufIdx);
    }

    private static String decodeComponent(String str,
                                          int firstEscaped,
                                          int toExcluded,
                                          Charset charset,
                                          boolean isPath,
                                          byte[] byteBuf,
                                          char[] charBuf,
                                          int charBufIdx) {
        int byteBufIdx;
        for (int i = firstEscaped; i < toExcluded; i++) {
            char c = str.charAt(i);
            if (c != '%') {
                charBuf[charBufIdx++] = c != '+' || isPath ? c : SPACE;
                continue;
            }

            byteBufIdx = 0;
            do {
                if (i + 3 > toExcluded) {
                    throw new IllegalArgumentException("unterminated escape sequence at index " + i + " of: " + str);
                }
                byteBuf[byteBufIdx++] = decodeHexByte(str, i + 1);
                i += 3;
            } while (i < toExcluded && str.charAt(i) == '%');
            i--;

            String decodedStr = new String(byteBuf, 0, byteBufIdx, charset);
            decodedStr.getChars(0, decodedStr.length(), charBuf, charBufIdx);
            charBufIdx += decodedStr.length();
        }
        return new String(charBuf, 0, charBufIdx);
    }

    private static int findPathEndIndex(String uri) {
        int len = uri.length();
        for (int i = 0; i < len; i++) {
            char c = uri.charAt(i);
            if (c == '?' || c == '#') {
                return i;
            }
        }
        return len;
    }

    private static final class TempBuf {

        private final char[] chars;

        private final byte[] bytes;

        public TempBuf(int bufSize) {
            this.chars = new char[bufSize];
            this.bytes = new byte[bufSize];
        }

        public char[] charBuf(int size) {
            if (size <= chars.length) {
                return chars;
            }
            return new char[size];
        }

        public byte[] byteBuf(int size) {
            if (size <= bytes.length) {
                return bytes;
            }
            return new byte[size];
        }
    }
}
