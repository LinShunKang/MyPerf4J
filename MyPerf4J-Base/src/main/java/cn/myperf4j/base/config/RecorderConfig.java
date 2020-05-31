package cn.myperf4j.base.config;

import cn.myperf4j.base.constant.PropertyValues;
import cn.myperf4j.base.util.MapUtils;

import java.util.Map;

import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.constant.PropertyKeys.Recorder.BACKUP_COUNT;
import static cn.myperf4j.base.constant.PropertyKeys.Recorder.MODE;
import static cn.myperf4j.base.constant.PropertyKeys.Recorder.SIZE_TIMING_ARR;
import static cn.myperf4j.base.constant.PropertyKeys.Recorder.SIZE_TIMING_MAP;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class RecorderConfig {

    private String mode;

    private int backupCount;

    private int timingArrSize;

    private int timingMapSize;

    private ProfilingParams commonProfilingParams;

    private final Map<String, ProfilingParams> profilingParamsMap = MapUtils.createHashMap(1024);

    public String mode() {
        return mode;
    }

    public void mode(String mode) {
        this.mode = mode;
    }

    public boolean accurateMode() {
        return PropertyValues.Recorder.MODE_ACCURATE.equalsIgnoreCase(mode);
    }

    public int backupCount() {
        return backupCount;
    }

    public void backupCount(int backupCount) {
        this.backupCount = backupCount;
    }

    public int timingArrSize() {
        return timingArrSize;
    }

    public void timingArrSize(int timingArrSize) {
        this.timingArrSize = timingArrSize;
    }

    public int timingMapSize() {
        return timingMapSize;
    }

    public void timingMapSize(int timingMapSize) {
        this.timingMapSize = timingMapSize;
    }

    public void commonProfilingParams(ProfilingParams commonProfilingParams) {
        this.commonProfilingParams = commonProfilingParams;
    }

    public void addProfilingParam(String methodName, int timeThreshold, int outThresholdCount) {
        profilingParamsMap.put(methodName, ProfilingParams.of(timeThreshold, outThresholdCount));
    }

    public ProfilingParams getProfilingParam(String methodName) {
        ProfilingParams params = profilingParamsMap.get(methodName);
        if (params != null) {
            return params;
        }
        return commonProfilingParams;
    }

    @Override
    public String toString() {
        return "RecorderConfig{" +
                "mode='" + mode + '\'' +
                ", backupCount=" + backupCount +
                ", timingArrSize=" + timingArrSize +
                ", timingMapSize=" + timingMapSize +
                ", commonProfilingParams=" + commonProfilingParams +
                '}';
    }

    public static RecorderConfig loadRecorderConfig() {
        RecorderConfig config = new RecorderConfig();
        config.mode(getStr(MODE, PropertyValues.Recorder.MODE_ACCURATE));
        config.backupCount(getInt(BACKUP_COUNT, 1));
        config.timingArrSize(getInt(SIZE_TIMING_ARR, 1024));
        config.timingMapSize(getInt(SIZE_TIMING_MAP, 32));
        config.commonProfilingParams(ProfilingParams.of(config.timingArrSize(), config.timingMapSize()));
        return config;
    }
}
