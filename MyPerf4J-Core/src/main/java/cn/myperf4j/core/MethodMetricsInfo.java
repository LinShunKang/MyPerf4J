package cn.myperf4j.core;

/**
 * Created by LinShunkang on 2019/07/23
 */
public class MethodMetricsInfo {

    private long tp95Sum;

    private long tp99Sum;

    private long tp999Sum;

    private long tp9999Sum;

    private int count;

    public MethodMetricsInfo(int tp95, int tp99, int tp999, int tp9999) {
        this.tp95Sum = tp95;
        this.tp99Sum = tp99;
        this.tp999Sum = tp999;
        this.tp9999Sum = tp9999;
        this.count = 0;
    }

    public void add(int tp95, int tp99, int tp999, int tp9999) {
        tp95Sum += tp95;
        tp99Sum += tp99;
        tp999Sum += tp999;
        tp9999Sum += tp9999;
        count++;
    }

    public long getTp999Sum() {
        return tp999Sum;
    }

    public long getTp95Sum() {
        return tp95Sum;
    }

    public long getTp99Sum() {
        return tp99Sum;
    }

    public long getTp9999Sum() {
        return tp9999Sum;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "MethodMetricsInfo{" +
                "tp95Sum=" + tp95Sum +
                ", tp99Sum=" + tp99Sum +
                ", tp999Sum=" + tp999Sum +
                ", tp9999Sum=" + tp9999Sum +
                ", count=" + count +
                '}';
    }
}
