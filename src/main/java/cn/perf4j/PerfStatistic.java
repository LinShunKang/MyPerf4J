package cn.perf4j;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class PerfStatistic {

    private int minTime;

    private int maxTime;

    private TimingRecord tp50;

    private TimingRecord tp90;

    private TimingRecord tp95;

    private TimingRecord tp99;

    private TimingRecord tp999;

    private TimingRecord tp9999;

    private TimingRecord tp100;

    private PerfStatistic() {
        this.minTime = -1;
        this.maxTime = -1;
        this.tp50 = TimingRecord.getInstance();
        this.tp90 = TimingRecord.getInstance();
        this.tp95 = TimingRecord.getInstance();
        this.tp99 = TimingRecord.getInstance();
        this.tp999 = TimingRecord.getInstance();
        this.tp9999 = TimingRecord.getInstance();
        this.tp100 = TimingRecord.getInstance();
    }

    public int getMinTime() {
        return minTime;
    }

    public void setMinTime(int minTime) {
        this.minTime = minTime;
    }

    public int getMaxTime() {
        return maxTime;
    }

    public void setMaxTime(int maxTime) {
        this.maxTime = maxTime;
    }

    public TimingRecord getTp50() {
        return tp50;
    }

    public void setTp50(TimingRecord tp50) {
        this.tp50 = tp50;
    }

    public TimingRecord getTp90() {
        return tp90;
    }

    public void setTp90(TimingRecord tp90) {
        this.tp90 = tp90;
    }

    public TimingRecord getTp95() {
        return tp95;
    }

    public void setTp95(TimingRecord tp95) {
        this.tp95 = tp95;
    }

    public TimingRecord getTp99() {
        return tp99;
    }

    public void setTp99(TimingRecord tp99) {
        this.tp99 = tp99;
    }

    public TimingRecord getTp999() {
        return tp999;
    }

    public void setTp999(TimingRecord tp999) {
        this.tp999 = tp999;
    }

    public TimingRecord getTp9999() {
        return tp9999;
    }

    public void setTp9999(TimingRecord tp9999) {
        this.tp9999 = tp9999;
    }

    public TimingRecord getTp100() {
        return tp100;
    }

    public void setTp100(TimingRecord tp100) {
        this.tp100 = tp100;
    }

    public TimingRecord[] getTpArr() {
        TimingRecord[] tpArr = {tp50, tp90, tp95, tp99, tp999, tp9999, tp100};
        return tpArr;
    }

    public static PerfStatistic getInstance() {
        return new PerfStatistic();
    }

    @Override
    public String toString() {
        return "PerfStatistic{" +
                "minTime=" + minTime +
                ", maxTime=" + maxTime +
                ", tp50=" + tp50 +
                ", tp90=" + tp90 +
                ", tp95=" + tp95 +
                ", tp99=" + tp99 +
                ", tp999=" + tp999 +
                ", tp9999=" + tp9999 +
                ", tp100=" + tp100 +
                '}';
    }
}
