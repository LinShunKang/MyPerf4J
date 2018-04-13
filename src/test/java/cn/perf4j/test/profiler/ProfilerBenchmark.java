package cn.perf4j.test.profiler;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class ProfilerBenchmark {

    private static final String EMPTY_STR = "";

    public static void main(String[] args) throws InterruptedException {
//        ApplicationContext ctx = new ClassPathXmlApplicationContext("*.xml");
//        ProfilerTestApi profilerTestApi = ctx.getBean("profilerTestApi", ProfilerTestApi.class);
//        ProfilerTestApi profilerTestApi = ctx.getBean("profilerTestApiV2", ProfilerTestApi.class);


        ProfilerTestApi profilerTestApi = new ProfilerTestApiImpl();

        int times = 200000000;
        singleThread(profilerTestApi, times / 10);
        System.gc();
        TimeUnit.SECONDS.sleep(20);

        singleThread(profilerTestApi, times);
        System.gc();
        TimeUnit.SECONDS.sleep(20);

        multiThread(profilerTestApi, 2, times);
        System.gc();
        TimeUnit.SECONDS.sleep(20);

        multiThread(profilerTestApi, 4, times);
        System.gc();
        TimeUnit.SECONDS.sleep(20);

        multiThread(profilerTestApi, 8, times);
        System.gc();
        TimeUnit.SECONDS.sleep(20);

    }

    private static void singleThread(ProfilerTestApi profilerTestApi, int times) throws InterruptedException {
        System.out.println("singleThread(" + profilerTestApi + ", " + times + ") start!!!");
        for (int i = 0; i < times; ++i) {
            profilerTestApi.test1(EMPTY_STR);
//            profilerTestApi.test2();
//            profilerTestApi.test3(EMPTY_STR, EMPTY_STR);
        }
//        System.out.println(stopWatch.lap("ProfilerBenchmark.test3(String, String)", String.valueOf(times)));

        System.out.println("singleThread(" + profilerTestApi + ", " + times + ") done!!!");
    }

    private static void multiThread(final ProfilerTestApi profilerTestApi, int threadCount, final int times) {
        System.out.println("multiThread(" + profilerTestApi + ", " + threadCount + ", " + times + ") start!!!");
        final CountDownLatch countDownLatch = new CountDownLatch(threadCount);
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));
        for (int i = 0; i < threadCount; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        singleThread(profilerTestApi, times);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("multiThread(" + profilerTestApi + ", " + threadCount + ", " + times + ") done!!!");
    }

}
