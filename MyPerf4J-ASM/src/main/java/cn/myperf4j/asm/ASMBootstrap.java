package cn.myperf4j.asm;

import cn.myperf4j.asm.aop.ProfilingAspect;
import cn.myperf4j.core.AbstractBootstrap;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.base.config.ProfilingConfig;
import cn.myperf4j.base.util.Logger;


/**
 * Created by LinShunkang on 2018/4/19
 */
public class ASMBootstrap extends AbstractBootstrap {

    private static final ASMBootstrap instance = new ASMBootstrap();

    private ASMBootstrap() {
        //empty
    }

    public static ASMBootstrap getInstance() {
        return instance;
    }

    @Override
    public AbstractRecorderMaintainer doInitRecorderMaintainer() {
        boolean accurateMode = ProfilingConfig.getInstance().isAccurateMode();
        long milliTimeSlice = ProfilingConfig.getInstance().getMilliTimeSlice();
        int backupRecorderCount = ProfilingConfig.getInstance().getBackupRecorderCount();

        ASMRecorderMaintainer maintainer = ASMRecorderMaintainer.getInstance();
        if (maintainer.initial(processor, accurateMode, backupRecorderCount, milliTimeSlice)) {
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
            ProfilingAspect.setRecorderMaintainer((ASMRecorderMaintainer) maintainer);
            ProfilingAspect.setRunning(true);
            return true;
        } catch (Exception e) {
            Logger.error("ASMBootstrap.initProfilerAspect()", e);
        }
        return false;
    }

}
