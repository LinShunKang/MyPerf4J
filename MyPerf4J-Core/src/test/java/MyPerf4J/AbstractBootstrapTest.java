package MyPerf4J;

import cn.myperf4j.base.config.ProfilingParams;
import cn.myperf4j.base.constant.PropertyKeys;
import cn.myperf4j.base.file.AutoRollingFileWriter;
import cn.myperf4j.base.file.MinutelyRollingFileWriter;
import cn.myperf4j.core.AbstractBootstrap;
import cn.myperf4j.core.recorder.AbstractRecorderMaintainer;
import cn.myperf4j.core.recorder.Recorders;
import org.junit.Assert;
import org.junit.Test;

import static cn.myperf4j.base.constant.PropertyValues.METRICS_PROCESS_TYPE_INFLUX_DB;

/**
 * Created by LinShunkang on 2018/10/20
 */
public class AbstractBootstrapTest {

    @Test
    public void test() {
        initPropertiesFile(METRICS_PROCESS_TYPE_INFLUX_DB);
        boolean initial = new AbstractBootstrap() {
            @Override
            public AbstractRecorderMaintainer doInitRecorderMaintainer() {
                return new AbstractRecorderMaintainer() {
                    @Override
                    public boolean initOther() {
                        return true;
                    }

                    @Override
                    public void addRecorder(int methodTagId, ProfilingParams params) {
                        for (int i = 0; i < recordersList.size(); ++i) {
                            Recorders recorders = recordersList.get(i);
                            recorders.setRecorder(methodTagId, createRecorder(methodTagId, params.getMostTimeThreshold(), params.getOutThresholdCount()));
                        }
                    }
                };
            }

            @Override
            public boolean initOther() {
                return true;
            }
        }.initial();

        Assert.assertTrue(initial);

    }

    private void initPropertiesFile(int metricsProcessorType) {
        String propertiesFile = "/tmp/MyPerf4J.properties";
        System.setProperty(PropertyKeys.PRO_FILE_NAME, propertiesFile);
        AutoRollingFileWriter writer = new MinutelyRollingFileWriter(propertiesFile, 1);
        writer.write("AppName=Test\n");
        writer.write("IncludePackages=MyPerf4J\n");
        writer.write("MetricsProcessorType=" + metricsProcessorType + "\n");
        writer.write("MilliTimeSlice=1000\n");
        writer.closeFile(true);
    }
}
