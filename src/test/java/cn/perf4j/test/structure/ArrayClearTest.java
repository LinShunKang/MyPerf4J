package cn.perf4j.test.structure;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/5
 */
public class ArrayClearTest {

    private static final int[] ZERO_ARR = new int[1024 * 1024];

    public static void main(String[] args) {
        int[] arr = new int[1024];
//        int[] arr = new int[102];

//        for (int i = 0; i < 100000; ++i) {
//            test1(arr);
//        }

        test3(arr, 1000);
        test3(arr, 10000);
        test3(arr, 100000);
        test3(arr, 1000000);
        test3(arr, 10000000);
        test3(arr, 100000000);
        test3(arr, 1000000000);
    }

    private static void test1(int[] arr) {
        Arrays.fill(arr, 1);

        long startNanos = System.nanoTime();
        clearArr(arr);
        long clearArrCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("clearArr() cost:" + clearArrCost + "us");

        Arrays.fill(arr, 1);

        startNanos = System.nanoTime();
        Arrays.fill(arr, 0);
        long fillCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("Arrays.fill() cost:" + fillCost + "us");

        System.out.println("----------------(clearArrCost <= fillCost)=" + (clearArrCost <= fillCost) + "");
    }

    private static void test2(int[] arr) {
        Arrays.fill(arr, 1);
        gc();

        long startNanos = System.nanoTime();
        clearArr(arr);
        long clearArrCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("clearArr() cost:" + clearArrCost + "us");

        Arrays.fill(arr, 1);
        gc();

        startNanos = System.nanoTime();
        Arrays.fill(arr, 0);
        long fillCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("Arrays.fill() cost:" + fillCost + "us");

//        gc();
        System.out.println("----------------(clearArrCost <= fillCost)=" + (clearArrCost <= fillCost) + "");
    }

    private static void test3(int[] arr, int times) {
        Arrays.fill(arr, 1);
        gc();

        long startNanos = System.nanoTime();
        for (int i = 0; i < times; ++i) {
            clearArr(arr);
        }
        long clearArrCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("clearArr() cost:" + clearArrCost + "us");

        Arrays.fill(arr, 1);
        gc();

        startNanos = System.nanoTime();
        for (int i = 0; i < times; ++i) {
            Arrays.fill(arr, 0);
        }
        long fillCost = (System.nanoTime() - startNanos) / 1000;
        System.out.println("Arrays.fill() cost:" + fillCost + "us");

//        gc();
        System.out.println("----------------(clearArrCost <= fillCost)=" + (clearArrCost <= fillCost) + "");
    }

    private static void clearArr(int[] arr) {
        if (arr == null || arr.length <= 0) {
            return;
        }

        if (arr.length <= ZERO_ARR.length) {
            System.arraycopy(ZERO_ARR, 0, arr, 0, arr.length);
            return;
        }

        int leftLength = arr.length;
        for (int i = 0; leftLength > 0; ++i) {
            System.arraycopy(ZERO_ARR, 0, arr, i * ZERO_ARR.length, (leftLength <= ZERO_ARR.length) ? leftLength : ZERO_ARR.length);
            leftLength = leftLength - ZERO_ARR.length;
        }
    }

    private static void gc() {
        System.gc();
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
        }
    }
}
