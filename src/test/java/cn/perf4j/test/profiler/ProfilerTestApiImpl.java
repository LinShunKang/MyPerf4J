package cn.perf4j.test.profiler;

import cn.perf4j.aop.Profiler;

/**
 * Created by LinShunkang on 2018/3/11
 */
@Profiler(mostTimeThreshold = 10)
public class ProfilerTestApiImpl implements ProfilerTestApi {

    @Override
    @Profiler(mostTimeThreshold = 20)
    public String test1(String aaa) {
        return null;
    }

    @Override
    @Profiler(mostTimeThreshold = 10)
    public int test2() {
        return 0;
    }

    @Override
    public void test3(String aaa, String bbb) {
    }
}
