package cn.perf4j.test.profiler;

import cn.perf4j.aop.Profiler;

/**
 * Created by LinShunkang on 2018/3/11
 */
@Profiler(mostTimeThreshold = 10, outThresholdCount = 10)
public class ProfilerTestApiImpl implements ProfilerTestApi {

    @Override
    @Profiler(mostTimeThreshold = 20, outThresholdCount = 10)
    public String test1(String aaa) {
//        System.out.println("ProfilerTestApiImpl.test1(" + aaa + ")");
//        try {
//            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(100));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return null;
    }

    @Override
    @Profiler(mostTimeThreshold = 10, outThresholdCount = 10)
    public int test2() {
//        System.out.println("ProfilerTestApiImpl.test2()");
//        try {
//            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(30));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        return 0;
    }


    @Override
//    @Profiler(mostTimeThreshold = 20, outThresholdCount = 10)
    public void test3(String aaa, String bbb) {
//        System.out.println("ProfilerTestApiImpl.test1(" + aaa + ", " + bbb + ")");
//        try {
//            TimeUnit.MILLISECONDS.sleep(ThreadLocalRandom.current().nextInt(50));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
    }
}
