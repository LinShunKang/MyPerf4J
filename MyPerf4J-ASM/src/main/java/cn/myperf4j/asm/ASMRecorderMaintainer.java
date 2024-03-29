package cn.myperf4j.asm;

import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import cn.myperf4j.core.recorder.Recorders;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class ASMRecorderMaintainer extends AbstractRecorderMaintainer {

    private static final ASMRecorderMaintainer instance = new ASMRecorderMaintainer();

    public static ASMRecorderMaintainer getInstance() {
        return instance;
    }

    @Override
    public boolean initOther() {
        return true;
    }

    @Override
    public void addRecorder(int methodTagId, ProfilingParams params) {
        for (int i = 0, size = recordersList.size(); i < size; ++i) {
            final Recorders recorders = recordersList.get(i);
            recorders.setRecorder(methodTagId, createRecorder(methodTagId, params));
        }
    }
}
