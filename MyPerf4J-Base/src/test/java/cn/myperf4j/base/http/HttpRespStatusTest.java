package cn.myperf4j.base.http;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static cn.myperf4j.base.http.HttpRespStatus.BAD_REQUEST;
import static cn.myperf4j.base.http.HttpRespStatus.CONTINUE;
import static cn.myperf4j.base.http.HttpRespStatus.INTERNAL_SERVER_ERROR;
import static cn.myperf4j.base.http.HttpRespStatus.MOVED_PERMANENTLY;
import static cn.myperf4j.base.http.HttpRespStatus.MULTIPLE_CHOICES;
import static cn.myperf4j.base.http.HttpRespStatus.OK;

/**
 * Created by LinShunkang on 2020/05/16
 */
public class HttpRespStatusTest {

    @Test
    public void testValueOf() {
        Assertions.assertEquals(CONTINUE, HttpRespStatus.valueOf(100));
        Assertions.assertEquals(OK, HttpRespStatus.valueOf(200));
        Assertions.assertEquals(MULTIPLE_CHOICES, HttpRespStatus.valueOf(300));
        Assertions.assertEquals(MOVED_PERMANENTLY, HttpRespStatus.valueOf(301));
        Assertions.assertEquals(BAD_REQUEST, HttpRespStatus.valueOf(400));
        Assertions.assertEquals(INTERNAL_SERVER_ERROR, HttpRespStatus.valueOf(500));

        Assertions.assertEquals(100, HttpRespStatus.valueOf(100).code());
        Assertions.assertEquals(200, HttpRespStatus.valueOf(200).code());
    }
}
