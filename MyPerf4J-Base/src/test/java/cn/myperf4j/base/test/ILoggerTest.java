package cn.myperf4j.base.test;

import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;
import org.junit.Assert;
import org.junit.Test;

public class ILoggerTest {

    @Test
    public void test() {
        ProfilingConfig.getInstance().setLogRollingTimeUnit(PropertyValues.LOG_ROLLING_TIME_MINUTELY);
        ProfilingConfig.getInstance().setLogReserveCount(PropertyValues.DEFAULT_LOG_RESERVE_COUNT);

        test(LoggerFactory.getLogger("/tmp/testLogger.log"));
        test(LoggerFactory.getLogger(PropertyValues.NULL_FILE));
    }

    private void test(ILogger logger) {
        logger.log("111111111");
        logger.log("222222222");
        logger.log("333333333");
        logger.log("444444444");
        logger.flushLog();

        logger.logAndFlush("555555");

        logger.preCloseLog();
        logger.closeLog();
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; ++i) {
            final String file = "/tmp/testLogger.log";
            new Thread(new Runnable() {
                @Override
                public void run() {
                    writeFile(file);
                }
            }).start();
        }
    }

    private static void writeFile(String file) {
        ILogger logger = LoggerFactory.getLogger(file);
        Assert.assertNotNull(logger);

        logger.log("111111111");
        logger.log("222222222");
        logger.log("333333333");
        logger.log("444444444");

        for (int i = 0; i < 1000000; ++i) {
            logger.log(Thread.currentThread().getName() + ": " + i);
        }
    }
}
