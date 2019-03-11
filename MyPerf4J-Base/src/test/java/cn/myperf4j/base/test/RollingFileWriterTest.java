package cn.myperf4j.base.test;

import cn.myperf4j.base.util.file.AutoRollingFileWriter;
import cn.myperf4j.base.util.file.DailyRollingFileWriter;
import cn.myperf4j.base.util.file.HourlyRollingFileWriter;
import cn.myperf4j.base.util.file.MinutelyRollingFileWriter;
import org.junit.Test;

/**
 * Created by LinShunkang on 2018/10/17
 */
public class RollingFileWriterTest {

    @Test
    public void test() {
        AutoRollingFileWriter writer1 = new MinutelyRollingFileWriter("/tmp/test1.log", 1);
        test(writer1);

        AutoRollingFileWriter writer2 = new HourlyRollingFileWriter("/tmp/test2.log", 1);
        test(writer2);

        AutoRollingFileWriter writer3 = new DailyRollingFileWriter("/tmp/test3.log", 1);
        test(writer3);
    }

    private void test(AutoRollingFileWriter writer) {
        writer.write("111111");
        writer.write("222222");
        writer.write("333333");
        writer.writeAndFlush("44444");
        writer.preCloseFile();
        writer.closeFile(true);
    }
}
