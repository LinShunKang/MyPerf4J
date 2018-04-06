package cn.perf4j.test.profiler;

import org.springframework.beans.factory.InitializingBean;

/**
 * Created by LinShunkang on 2018/3/11
 */
//@Profiler(mostTimeThreshold = 10)
//@NonProfiler
public class ProfilerTestApiImplV2 implements ProfilerTestApi, InitializingBean {

    @Override
//    @Profiler(mostTimeThreshold = 20)
    public String test1(String aaa) {
        return null;
    }

    @Override
//    @Profiler(mostTimeThreshold = 10)
    public int test2() {
        return 0;
    }

    @Override
    public void test3(String aaa, String bbb) {
    }

    @Override
//    @NonProfiler
    public void afterPropertiesSet() throws Exception {

    }
}
