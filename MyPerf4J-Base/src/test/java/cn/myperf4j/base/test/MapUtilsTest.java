package cn.myperf4j.base.test;

import cn.myperf4j.base.util.collections.MapUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class MapUtilsTest {

    @Test
    public void test() {
        Map<Object, Object> hashMap = MapUtils.createHashMap(1);
        Assertions.assertNotNull(hashMap);

        Map<Object, Object> hashMap2 = MapUtils.createHashMap(10, 0.01F);
        Assertions.assertNotNull(hashMap2);

        ConcurrentHashMap<Object, Object> concHashMap = MapUtils.createConcHashMap(10, 0.1F);
        Assertions.assertNotNull(concHashMap);
    }
}
