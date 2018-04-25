package cn.myperf4j.asm;

import cn.myperf4j.asm.aop.ProfilerAspect;
import cn.myperf4j.asm.aop.ProfilerTransformer;
import cn.myperf4j.core.MyBootstrap;
import cn.myperf4j.core.PropConstants;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MyProperties;

import java.lang.instrument.Instrumentation;

/**
 * Created by LinShunkang on 2018/4/19
 */
public class MyASMBootstrap {

    public static void premain(String options, Instrumentation ins) {
//        ins.addTransformer(new ProfilerTransformer());

        if (!MyBootstrap.initial()) {
            MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_NO);
            Logger.error("MyBootstrap initial failure!!!");
            return;
        }

        if (!initial()) {
            MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_NO);
            Logger.error("MyASMBootstrap initial failure!!!");
            return;
        }

        MyProperties.setStr(PropConstants.RUNNING_STATUS, PropConstants.RUNNING_STATUS_YES);
        Logger.info("MyASMBootstrap initial success!!!");

        ins.addTransformer(new ProfilerTransformer());

    }

    private static boolean initial() {
        try {
            if (!initProfilerAspect()) {
                return false;
            }

            return true;
        } catch (Exception e) {
            Logger.error("MyASMBootstrap.initial()", e);
        }
        return false;
    }

    private static boolean initProfilerAspect() {
        try {
            ProfilerAspect.setRecorderMaintainer(MyBootstrap.maintainer);
            ProfilerAspect.setRunning(true);
            return true;
        } catch (Exception e) {
            Logger.error("MyASMBootstrap.initProfilerAspect()", e);
        }
        return false;
    }


}
