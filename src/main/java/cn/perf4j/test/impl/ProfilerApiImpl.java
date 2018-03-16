package cn.perf4j.test.impl;

import cn.perf4j.aop.Profiler;
import cn.perf4j.test.ProfilerApi;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/11
 */
@Profiler(mostTimeThreshold = 10, outThresholdCount = 10)
public class ProfilerApiImpl implements ProfilerApi {

    @Override
    @Profiler(mostTimeThreshold = 20, outThresholdCount = 10)
    public String test1(String aaa) {
//        System.out.println("ProfilerApiImpl.test1(" + aaa + ")");
//        try {
//            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    @Profiler(mostTimeThreshold = 20, outThresholdCount = 10)
    public void test1(String aaa, String bbb) {
//        System.out.println("ProfilerApiImpl.test1(" + aaa + ", " + bbb + ")");
//        try {
//            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(50));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }

    @Override
//    @Profiler(mostTimeThreshold = 10, outThresholdCount = 10)
    public int test2() {
//        System.out.println("ProfilerApiImpl.test2()");
        try {
            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(30));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
