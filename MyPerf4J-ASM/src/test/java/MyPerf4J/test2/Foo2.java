package MyPerf4J.test2;

import cn.myperf4j.base.annotation.NonProfiler;
import cn.myperf4j.base.annotation.Profiler;

import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/4/23
 */
public class Foo2 {

    private static void test0() {
        System.out.println("Foo2.test0()");
    }

    @Profiler
    public static void test1() {
        System.out.println("Foo2.test1()");
    }

    @Profiler(mostTimeThreshold = 200, outThresholdCount = 20)
    public static void test2() {
        try {
            System.out.println("Foo2.test2().try");
        } catch (Exception e) {
            System.out.println("Foo2.test2().catch");
            e.printStackTrace();
        }
    }

    @NonProfiler
    public static void test3() {
        try {
            System.out.println("Foo2.test3().try");
        } finally {
            System.out.println("Foo2.test3().finally");
        }
    }

    @Profiler
    @NonProfiler
    public static void test4() {
        try {
            TimeUnit.SECONDS.sleep(1);
            System.out.println("Foo2.test4().try");
        } catch (InterruptedException e) {
            System.out.println("Foo2.test4().catch");
            e.printStackTrace();
        } finally {
            System.out.println("Foo2.test4().finally");
        }
    }

    @NonProfiler
    @Profiler
    public static String test5(int i) {
        try {
            if (i == 1) {
                return "1";
            } else if (i == 2) {
                return "2";
            } else if (i == 3) {
                return "3";
            }

            TimeUnit.SECONDS.sleep(1);
            System.out.println("Foo2.test5().try");
        } catch (InterruptedException e) {
            System.out.println("Foo2.test5().catch");
            e.printStackTrace();
        } finally {
            System.out.println("Foo2.test5().finally");
        }
        return "ELSE";
    }

    public static String test6(int i) {
        if (i == 1) {
            return "1";
        } else if (i == 2) {
            return "2";
        } else if (i == 3) {
            return "3";
        }
        return "ELSE";
    }
}
