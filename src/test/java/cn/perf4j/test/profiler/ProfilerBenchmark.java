package cn.perf4j.test.profiler;

import cn.perf4j.RecorderContainer;
import cn.perf4j.util.StopWatch;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class ProfilerBenchmark {

    public static void main(String[] args) throws InterruptedException {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("*.xml");
//        singleThread(ctx);
        multiThread(ctx, 8);

    }

    private static void singleThread(ApplicationContext ctx) throws InterruptedException {
        ProfilerApi profilerApi = ctx.getBean("profilerApi", ProfilerApi.class);
        StopWatch stopWatch = new StopWatch();
        int times = 100000000;
        for (int i = 0; i < times; ++i) {
            profilerApi.test1("1");
            profilerApi.test3("1", "2");
        }
        System.out.println(stopWatch.lap("ProfilerBenchmark.test3(String, String)", String.valueOf(times)));

        TimeUnit.SECONDS.sleep(30);

        stopWatch.start();
        for (int i = 0; i < times; ++i) {
            profilerApi.test1("1");
            profilerApi.test2();
        }
        System.out.println(stopWatch.lap("ProfilerBenchmark.test1(String)2", String.valueOf(times)));

        RecorderContainer recorderContainer = ctx.getBean("recorderContainer", RecorderContainer.class);
        System.out.println(recorderContainer.getRecorderMap());
    }

    private static void multiThread(final ApplicationContext ctx, int threadCount) {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(threadCount, threadCount, 1, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(1000));
        for (int i = 0; i < threadCount; ++i) {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        singleThread(ctx);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
