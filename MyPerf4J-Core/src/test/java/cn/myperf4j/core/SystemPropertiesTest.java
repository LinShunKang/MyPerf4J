package cn.myperf4j.core;

import org.junit.Test;

/**
 * Created by LinShunkang on 2019/07/28
 */
public class SystemPropertiesTest {

    @Test
    public void test() {
        System.out.println(System.getProperty("os.name"));
        System.out.println(System.getProperty("file.separator"));
        System.out.println(System.getProperty("path.separator"));
        System.out.println(System.getProperty("line.separator"));
    }

}
