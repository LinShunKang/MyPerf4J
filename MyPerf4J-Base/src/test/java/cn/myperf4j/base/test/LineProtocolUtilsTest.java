package cn.myperf4j.base.test;

import cn.myperf4j.base.util.LineProtocolUtils;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class LineProtocolUtilsTest {

    @Test
    public void test() {
        String expect = "method_metrics\\,AppName\\=TestApp\\,ClassName\\=TestClass\\,Method\\=TestClass.test\\ RPS\\=1i\\,Avg\\=0.00\\,Min\\=0i\\,Max\\=0i\\,StdDev\\=0.00\\,Count\\=17i\\,TP50\\=0i\\,TP90\\=0i\\,TP95\\=0i\\,TP99\\=0i\\,TP999\\=0i\\,TP9999\\=0i\\,TP99999\\=0i\\,TP100\\=0i\\ 1539705590006000000";
        Assert.assertEquals(expect, LineProtocolUtils.processTagOrField("method_metrics,AppName=TestApp,ClassName=TestClass,Method=TestClass.test RPS=1i,Avg=0.00,Min=0i,Max=0i,StdDev=0.00,Count=17i,TP50=0i,TP90=0i,TP95=0i,TP99=0i,TP999=0i,TP9999=0i,TP99999=0i,TP100=0i 1539705590006000000"));
    }
}
