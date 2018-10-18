package cn.myperf4j.base.test;

import cn.myperf4j.base.util.Logger;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class LoggerTest {

    @Test
    public void test() {
        Logger.setDebugEnable(true);
        Logger.debug("debug test");
        Logger.info("info test");
        Logger.warn("warn test");
        Logger.error("error test");
        Logger.error("error test", new UnknownError());
    }
}
