package cn.myperf4j.base.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.http.HttpMethod.GET;
import static cn.myperf4j.base.http.HttpMethod.HEAD;
import static cn.myperf4j.base.http.HttpMethod.POST;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpMethodTest {

    @Test
    public void testPermitsBody() {
        Assertions.assertFalse(HEAD.isPermitsBody());
        Assertions.assertFalse(GET.isPermitsBody());
        Assertions.assertTrue(POST.isPermitsBody());
    }
}
