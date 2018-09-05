package cn.myperf4j.base.test;

import cn.myperf4j.base.log.ILogger;
import cn.myperf4j.base.log.LoggerFactory;

public class ILoggerTest {

    public static void main(String[] args) {
        for (int i = 0; i < 100; ++i) {
            final String file = "/tmp/testLogger.log" + i;
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
        logger.log("111111111");
        logger.log("222222222");
        logger.log("333333333");
        logger.log("444444444");

        for (int i = 0; i < 100000000; ++i) {
            logger.log(String.valueOf(i));
        }
    }
}
