package cn.perf4j;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class TimingRecord {

    private int time;

    private int count;

    private TimingRecord(int time, int count) {
        this.time = time;
        this.count = count;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "TimingRecord{" +
                "time=" + time +
                ", count=" + count +
                '}';
    }

    public static TimingRecord getInstance() {
        return getInstance(-1, -1);
    }

    public static TimingRecord getInstance(int time, int count) {
        return new TimingRecord(time, count);
    }
}
