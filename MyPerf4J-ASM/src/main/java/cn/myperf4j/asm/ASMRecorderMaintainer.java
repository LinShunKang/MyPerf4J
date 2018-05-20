package cn.myperf4j.asm;

import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.TagMaintainer;
import cn.myperf4j.core.config.ProfilerParams;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class ASMRecorderMaintainer extends AbstractRecorderMaintainer {

    private static final ASMRecorderMaintainer instance = new ASMRecorderMaintainer();

    private ASMRecorderMaintainer() {
        recorders = new AtomicReferenceArray<>(TagMaintainer.MAX_NUM);
        backupRecorders = new AtomicReferenceArray<>(TagMaintainer.MAX_NUM);
    }

    public static ASMRecorderMaintainer getInstance() {
        return instance;
    }

    @Override
    public boolean initRecorderMap() {
        return true;
    }

    @Override
    public boolean initOther() {
        return true;
    }

    public void addRecorder(int tagId, String tag, ProfilerParams params) {
        synchronized (locker) {
            recorders.set(tagId, createRecorder(tag, params.getMostTimeThreshold(), params.getOutThresholdCount()));
            backupRecorders.set(tagId, createRecorder(tag, params.getMostTimeThreshold(), params.getOutThresholdCount()));
        }
    }
}
