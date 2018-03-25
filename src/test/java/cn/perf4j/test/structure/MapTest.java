package cn.perf4j.test.structure;

import cn.perf4j.util.MapUtils;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by LinShunkang on 2018/3/17
 */
public class MapTest {

    public static void main(String[] args) {
        ConcurrentHashMap<String, String> map = MapUtils.createConcHashMap(10, 0.5F);
        System.out.println(map.putIfAbsent("a", "1"));
        System.out.println(map.putIfAbsent("a", "2"));
        System.out.println(map.putIfAbsent("a", "3"));
    }
}
