package cn.myperf4j.base.io;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Created by LinShunkang on 2026/06/24
 */
public class BytesBuilderTest {

    @Test
    public void testAppendChar() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            final String value = "A\ud83d\u4F60好";
            for (int i = 0; i < value.length(); i++) {
                builder.append(value.charAt(i));
            }

            assertBytesEquals(value.getBytes(UTF_8), builder.toBytes());
        }
    }

    @Test
    public void testAppendSurrogateChar() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            final char value = '\uD83D';
            builder.append(value);
            assertBytesEquals(String.valueOf(value).getBytes(UTF_8), builder.toBytes());
        }
    }

    private static void assertBytesEquals(byte[] expected, Bytes actual) {
        Assertions.assertEquals(expected.length, actual.length());
        Assertions.assertArrayEquals(expected, Arrays.copyOf(actual.bytes(), actual.length()));
    }

    @Test
    public void testAppendByte() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append((byte) 48);
            builder.append((byte) 49);
            builder.append((byte) 50);
            Assertions.assertEquals("012", builder.toString());
        }
    }

    @Test
    public void testAppendInt() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append(-0);
            builder.append(-1);
            builder.append(2);
            Assertions.assertEquals("0-12", builder.toString());
        }
    }

    @Test
    public void testAppendLong() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append(-0L);
            builder.append(1L);
            builder.append(-2L);
            Assertions.assertEquals("01-2", builder.toString());
        }
    }

    @Test
    public void testAppendFloat() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append(-0F);
            builder.append(-1F);
            builder.append(2F);
            Assertions.assertEquals("-0.0-1.02.0", builder.toString());
        }
    }

    @Test
    public void testAppendDouble() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append(-0D);
            builder.append(1D);
            builder.append(-2D);
            Assertions.assertEquals("-0.01.0-2.0", builder.toString());
        }
    }

    @Test
    public void testAppendCharSimple() {
        try (BytesBuilder builder = new BytesBuilder(1)) {
            builder.append('0');
            builder.append('1');
            builder.append('A');
            builder.append('你');
            builder.append('こ');
            builder.append('안');
            Assertions.assertEquals("01A你こ안", builder.toString());
        }
    }

    @Test
    public void testFullApi() {
        try (BytesBuilder bb = new BytesBuilder(128)) {
            bb.append((byte) 1);
            bb.append("你好世界こんにちは안녕하세요");
            bb.append(',');
            bb.append("😂👍😊✨🚀✅");
            bb.append(1234);
            bb.append(',');
            bb.append(1234567890L);
            bb.append("HELLO, WORLD!");
            bb.append("你好，世界！");
            bb.append(1.0 / 3F);
            bb.append(',');
            bb.append(-2.0 / 3D);
            bb.append(',');
            bb.append('你');
            Assertions.assertEquals(bb.toBytes().toString(UTF_8), bb.toString());
            System.out.println(bb.toBytes().toString(UTF_8));
            System.out.println(bb);
        }
    }

    @Test
    public void testEmoji() {
        final String str = "😂";
        final byte[] bytes = str.getBytes(UTF_8);
        System.out.println(Arrays.toString(bytes));
        System.out.println(str.length());

        System.out.println(Integer.toHexString(str.charAt(0)));
        System.out.println(Integer.toHexString(str.charAt(1)));
        System.out.println(new String(new char[]{'\ud83d', '\ude02'}));

        System.out.println(String.valueOf(-0D));
        System.out.println(String.valueOf(-0));
    }
}
