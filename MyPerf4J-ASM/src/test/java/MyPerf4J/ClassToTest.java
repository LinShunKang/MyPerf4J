package MyPerf4J;

import cn.myperf4j.base.util.ThreadUtils;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/10/18
 */
public class ClassToTest {

    public String getStr() {
        ThreadUtils.sleepQuietly(ThreadLocalRandom.current().nextInt(10), TimeUnit.MILLISECONDS);
        return "111";
    }
}
