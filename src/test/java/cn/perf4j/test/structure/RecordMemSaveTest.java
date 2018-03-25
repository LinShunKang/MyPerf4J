package cn.perf4j.test.structure;

import cn.perf4j.util.MapUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Created by LinShunkang on 2018/3/25
 */
public class RecordMemSaveTest {
    public static void main(String[] args) {
        Map<Integer, AtomicInteger> timingMap = initMap();
        AtomicIntegerArray timingArr = initArr();

        int idx = 0;
        int[] sortedArr = new int[getEffectiveRecordCount(timingMap, timingArr) * 2];
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                sortedArr[idx++] = i;
                sortedArr[idx++] = count;
            }
        }

        System.out.println(Arrays.toString(fillMapRecord(timingMap, sortedArr, idx)));
    }

    private static int getEffectiveRecordCount(Map<Integer, AtomicInteger> timingMap, AtomicIntegerArray timingArr) {
        int result = 0;
        for (int i = 0; i < timingArr.length(); ++i) {
            int count = timingArr.get(i);
            if (count > 0) {
                result++;
            }
        }

        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                result++;
            }
        }
        return result;
    }

    private static Map<Integer, AtomicInteger> initMap() {
        Map<Integer, AtomicInteger> timingMap = MapUtils.createHashMap(10, 0.5F);
        timingMap.put(10, new AtomicInteger(110));
        timingMap.put(20, new AtomicInteger(120));
        timingMap.put(30, new AtomicInteger(130));
        timingMap.put(40, new AtomicInteger(140));
        timingMap.put(50, new AtomicInteger(150));
        timingMap.put(60, new AtomicInteger(160));
        timingMap.put(70, new AtomicInteger(0));
        return timingMap;
    }

    private static AtomicIntegerArray initArr() {
        AtomicIntegerArray arr = new AtomicIntegerArray(7);
        arr.set(0, 10);
        arr.set(1, 11);
        arr.set(2, 12);
        arr.set(3, 13);
        arr.set(4, 14);
        arr.set(5, 0);
        arr.set(6, 0);
        return arr;
    }

    private static int[] fillMapRecord(Map<Integer, AtomicInteger> timingMap, int[] arr, int offset) {
        int idx = offset;
        for (Map.Entry<Integer, AtomicInteger> entry : timingMap.entrySet()) {
            if (entry.getValue().get() > 0) {
                arr[idx++] = entry.getKey();
            }
        }

        Arrays.sort(arr, offset, idx);
        for (int i = idx - 1; i >= offset; --i) {
            arr[2 * i - offset] = arr[i];
            arr[2 * i + 1 - offset] = timingMap.get(arr[i]).get();
        }
        return arr;
    }
}
