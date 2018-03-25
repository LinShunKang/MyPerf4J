package cn.perf4j.test.structure;

import cn.perf4j.Record;
import cn.perf4j.util.MapUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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


        List<Record> records = new ArrayList<>(5);
        records.add(Record.getInstance(2, 1));
        records.add(Record.getInstance(1, 1));
        records.add(Record.getInstance(5, 1));
        records.add(Record.getInstance(4, 1));
        Collections.sort(records, new Comparator<Record>() {
            @Override
            public int compare(Record o1, Record o2) {
                return o1.getTime() - o2.getTime();
            }
        });
        System.out.println(records);
    }
}
