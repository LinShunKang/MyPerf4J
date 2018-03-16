package cn.perf4j.test.profiler;

import cn.perf4j.RecorderContainer;
import cn.perf4j.utils.StopWatch;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class ProfilerTest {

    public static void main(String[] args) {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("*.xml");
        ProfilerApi profilerApi = ctx.getBean("profilerApi", ProfilerApi.class);
//        ProfilerApi profilerApi = new ProfilerApiImpl();
        System.out.println(profilerApi.getClass());

        StopWatch stopWatch = new StopWatch();
        int times = 100000000;
//        int times = 10000000;
        for (int i = 0; i < times; ++i) {
            profilerApi.test1("1","2");
        }
        System.out.println(stopWatch.lap("ProfilerTest.test1(String, String)", String.valueOf(times)));


        for (int i = 0; i < times; ++i) {
            profilerApi.test1("1");
        }
        System.out.println(stopWatch.lap("ProfilerTest.test1(String)", String.valueOf(times)));

//        for (int i = 0; i < times; ++i) {
//            profilerApi.test1("1111", "222");
//        }
//
//        System.out.println();
//        for (int i = 0; i < times; ++i) {
//            profilerApi.test2();
//        }

        RecorderContainer recorderContainer = ctx.getBean("recorderContainer", RecorderContainer.class);
        System.out.println(recorderContainer.getRecorderMap());
    }

}
