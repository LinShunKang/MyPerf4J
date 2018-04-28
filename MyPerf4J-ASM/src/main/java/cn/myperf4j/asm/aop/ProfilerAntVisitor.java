package cn.myperf4j.asm.aop;

import cn.myperf4j.core.ProfilerParams;
import org.objectweb.asm.AnnotationVisitor;

/**
 * Created by LinShunkang on 2018/4/26
 */
public class ProfilerAntVisitor extends AnnotationVisitor {

    private ProfilerParams profilerParams;

    public ProfilerAntVisitor(int api, AnnotationVisitor av, ProfilerParams params) {
        super(api, av);

        if (params == null) {
            this.profilerParams = ProfilerParams.of(false);
        } else {
            this.profilerParams = ProfilerParams.of(params);
        }
    }

    public ProfilerParams getProfilerParams() {
        return profilerParams;
    }

    @Override
    public void visit(String name, Object value) {
        if ("mostTimeThreshold".equals(name)) {
            profilerParams.setMostTimeThreshold((Integer) value);
        } else if ("outThresholdCount".equals(name)) {
            profilerParams.setOutThresholdCount((Integer) value);
        }

        super.visit(name, value);
    }
}
