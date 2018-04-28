package cn.myperf4j.aspectj;

import cn.myperf4j.aspectj.aop.ProfilerAspect;
import cn.myperf4j.core.AbstractBootstrap;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.constant.PropertyKeys;
import cn.myperf4j.core.constant.PropertyValues;
import cn.myperf4j.core.util.Logger;
import cn.myperf4j.core.util.MyProperties;

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
        boolean accurateMode = MyProperties.isSame(PropertyKeys.RECORDER_MODE, PropertyValues.RECORDER_MODE_ACCURATE);
        long milliTimeSlice = MyProperties.getLong(PropertyKeys.MILL_TIME_SLICE, PropertyValues.DEFAULT_TIME_SLICE);

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
