package cn.myperf4j.base.test;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

/**
 * Created by LinShunkang on 2019/09/22
 */
public class CompilationTest {

    public static void main(String[] args) {
        CompilationMXBean mxBean = ManagementFactory.getCompilationMXBean();
        System.out.println(mxBean.getName());
        System.out.println(mxBean.getObjectName());
        System.out.println(mxBean.getTotalCompilationTime() + "ms");
    }
}
