package cn.perf4j.test.aspectJ;

import cn.perf4j.util.PerfStatsCalculator;

/**
 * Created by LinShunkang on 2018/4/6
 */
public class HelloAop {

    public static void main(String[] args) {

        UserService userService = new UserService();
        long length = 0L;
//        long times = Long.MAX_VALUE;
        long times = 256 * 1024 * 1024L;
//        long times = 10;
        MethodStartAspect.getRecorder().setStartTime(System.currentTimeMillis());
        for (long i = 0L; i < times; ++i) {
            length += userService.fetchUserById(i);
        }
        MethodStartAspect.getRecorder().setStopTime(System.currentTimeMillis());
        System.out.println(length);
        System.out.println(PerfStatsCalculator.calPerfStats(MethodStartAspect.getRecorder()));
    }
}