package cn.myperf4j.base.http;

import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.HEAD;
import static cn.myperf4j.base.http.HttpMethod.POST;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpMethodTest {

    @Test
    public void testPermitsBody() {
        Assert.assertFalse(HEAD.isPermitsBody());
        Assert.assertFalse(GET.isPermitsBody());
        Assert.assertTrue(POST.isPermitsBody());
    }

}
