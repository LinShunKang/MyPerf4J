package cn.myperf4j.aspectj;

import cn.myperf4j.aspectj.aop.ProfilerAspect;
import cn.myperf4j.core.AbstractBootstrap;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.config.ProfilingConfig;
import cn.myperf4j.core.util.Logger;

/**
 * Created by LinShunkang on 2018/4/19
 */
public class AspectJBootstrap extends AbstractBootstrap {

    private static final AspectJBootstrap instance = new AspectJBootstrap();

    private AspectJBootstrap() {
        //empty
    }

    public static AspectJBootstrap getInstance() {
        return instance;
    }

    @Override
    public AbstractRecorderMaintainer doInitRecorderMaintainer() {
        boolean accurateMode = ProfilingConfig.getInstance().isAccurateMode();
        long milliTimeSlice = ProfilingConfig.getInstance().getMilliTimeSlice();

        AbstractRecorderMaintainer maintainer = AspectJRecorderMaintainer.getInstance();
        if (maintainer.initial(processor, accurateMode, milliTimeSlice)) {
            return maintainer;
        }
        return null;
    }

    @Override
    public boolean initOther() {
        return initProfilerAspect();
    }

    private boolean initProfilerAspect() {
        try {
            ProfilerAspect.setRecorderMaintainer(maintainer);
            ProfilerAspect.setRunning(true);
            return true;
        } catch (Exception e) {
            Logger.error("AspectJBootstrap.initProfilerAspect()", e);
        }
        return false;
    }


}
