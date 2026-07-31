package cn.myperf4j.base.test;

import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.util.LineProtocolUtils.processTagOrField;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class LineProtocolUtilsTest {

    @Test
    public void test() {
        final String str = "method_metrics\\,AppName\\=TestApp\\,ClassName\\=TestClass\\,Method\\=TestClass.test\\ " +
                "RPS\\=1i\\,Avg\\=0.00\\,Min\\=0i\\,Max\\=0i\\,StdDev\\=0.00\\,Count\\=17i\\,TP50\\=0i\\,TP90\\=0i\\," +
                "TP95\\=0i\\,TP99\\=0i\\,TP999\\=0i\\,TP9999\\=0i\\,TP99999\\=0i\\,TP100\\=0i\\ 1539705590006000000";
        assertArrayEquals(str.getBytes(UTF_8),
                processTagOrField("method_metrics,AppName=TestApp,ClassName=TestClass,Method=TestClass.test " +
                        "RPS=1i,Avg=0.00,Min=0i,Max=0i,StdDev=0.00,Count=17i,TP50=0i,TP90=0i," +
                        "TP95=0i,TP99=0i,TP999=0i,TP9999=0i,TP99999=0i,TP100=0i 1539705590006000000"));
    }

    @Test
    public void testEscapeSpecialChars() {
        assertArrayEquals("a\\ b\\,c\\=d".getBytes(UTF_8), processTagOrField("a b,c=d"));
        assertArrayEquals("\\,\\ \\=".getBytes(UTF_8), processTagOrField(", ="));
    }

    @Test
    public void testNoEscapeChars() {
        final String str = "abcdefg";
        assertArrayEquals(str.getBytes(UTF_8), processTagOrField(str));
        assertArrayEquals(str.getBytes(UTF_8), processTagOrField(str));
    }
}
