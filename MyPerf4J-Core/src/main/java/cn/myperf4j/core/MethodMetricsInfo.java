package cn.myperf4j.core;

/**
 * Created by LinShunkang on 2019/07/23
 */
public class MethodMetricsInfo {

    private long tp90Sum;

    private long tp95Sum;

    private long tp99Sum;

    private int count;

    public MethodMetricsInfo(int tp90, int tp95, int tp99) {
        this.tp90Sum = tp90;
        this.tp95Sum = tp95;
        this.tp99Sum = tp99;
        this.count = 0;
    }

    public void add(int tp90, int tp95, int tp99) {
        tp90Sum += tp90;
        tp95Sum += tp95;
        tp99Sum += tp99;
        count++;
    }

    public long getTp90Sum() {
        return tp90Sum;
    }

    public long getTp95Sum() {
        return tp95Sum;
    }

    public long getTp99Sum() {
        return tp99Sum;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return "MethodMetricsInfo{" +
                "tp90Sum=" + tp90Sum +
                ", tp95Sum=" + tp95Sum +
                ", tp99Sum=" + tp99Sum +
                ", count=" + count +
                '}';
    }
}
