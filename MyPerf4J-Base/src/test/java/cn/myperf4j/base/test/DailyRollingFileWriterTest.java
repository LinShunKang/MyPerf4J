package cn.myperf4j.base.test;

import cn.myperf4j.base.util.file.DailyRollingFileWriter;

public class DailyRollingFileWriterTest {


    public static void main(String[] args) {
        DailyRollingFileWriter writer = new DailyRollingFileWriter("/tmp/test.log");

        writer.write("111111");
        writer.write("222222");
        writer.write("333333");
        writer.writeAndFlush("44444");
    }
}
