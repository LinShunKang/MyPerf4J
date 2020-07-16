package cn.myperf4j.base.config;

import cn.myperf4j.base.util.StrUtils;

import static cn.myperf4j.base.config.MyProperties.getBoolean;
import static cn.myperf4j.base.config.MyProperties.getStr;
import static cn.myperf4j.base.config.MyProperties.getInt;
import static cn.myperf4j.base.constant.PropertyKeys.Basic.APP_NAME;
import static cn.myperf4j.base.constant.PropertyKeys.Basic.PROPERTIES_FILE_DIR;
import static cn.myperf4j.base.constant.PropertyKeys.Basic.DEBUG;
import static cn.myperf4j.base.constant.PropertyKeys.Basic.ASM_COMPUTE_MODE;

/**
 * Created by LinShunkang on 2020/05/24
 */
public class BasicConfig {

    private String appName;

    private String configFileDir;

    private boolean debug;

    private Integer asmComputeMode;

    public String appName() {
        return appName;
    }

    public void appName(String appName) {
        if (StrUtils.isBlank(appName)) {
            throw new IllegalArgumentException("AppName is required!!!");
        }
        this.appName = appName;
    }

    public String configFileDir() {
        return configFileDir;
    }

    public void configFileDir(String configFileDir) {
        this.configFileDir = configFileDir;
    }

    public boolean debug() {
        return debug;
    }

    public void debug(boolean debug) {
        this.debug = debug;
    }

    public Integer asmComputeMode() {
        return asmComputeMode;
    }

    public void asmComputeMode(Integer asmComputeMode) {
        this.asmComputeMode = asmComputeMode;
    }

    public String sysProfilingParamsFile() {
        return configFileDir + "." + appName + "_SysGenProfilingFile";
    }

    @Override
    public String toString() {
        return "BasicConfig{" +
                "appName='" + appName + '\'' +
                ", configFileDir='" + configFileDir + '\'' +
                ", debug=" + debug +
                '}';
    }

    public static BasicConfig loadBasicConfig() {
        String appName = getStr(APP_NAME);
        if (StrUtils.isBlank(appName)) {
            throw new IllegalArgumentException(APP_NAME.key() + "|" + APP_NAME.legacyKey() + " is required!!!");
        }

        BasicConfig config = new BasicConfig();
        config.appName(appName);
        config.debug(getBoolean(DEBUG, false));
        config.configFileDir(getStr(PROPERTIES_FILE_DIR));
        config.asmComputeMode(getInt(ASM_COMPUTE_MODE));
        return config;
    }

}
