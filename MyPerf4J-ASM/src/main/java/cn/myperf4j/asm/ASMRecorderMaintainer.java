package cn.myperf4j.asm;

import cn.myperf4j.core.AbstractRecorder;
import cn.myperf4j.core.AbstractRecorderMaintainer;
import cn.myperf4j.core.config.ProfilerParams;
import cn.myperf4j.core.util.MapUtils;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class ASMRecorderMaintainer extends AbstractRecorderMaintainer {

    private static final ASMRecorderMaintainer instance = new ASMRecorderMaintainer();

    private ASMRecorderMaintainer() {
        //为了让recorderMap.get()更加快速，减小loadFactor->减少碰撞的概率->加快get()的执行速度
        recorderMap = MapUtils.createHashMap(10240, 0.2F);
        backupRecorderMap = MapUtils.createHashMap(10240, 0.2F);
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

    @Override
    public AbstractRecorder getRecorder(String api) {
        return recorderMap.get(api);
    }

    @Override
    public void addRecorder(String tag, ProfilerParams params) {
        synchronized (locker) {
            if (!recorderMap.containsKey(tag)) {
                recorderMap.put(tag, createRecorder(tag, params.getMostTimeThreshold(), params.getOutThresholdCount()));
            }

            if (!backupRecorderMap.containsKey(tag)) {
                backupRecorderMap.put(tag, createRecorder(tag, params.getMostTimeThreshold(), params.getOutThresholdCount()));
            }
        }
    }


}
