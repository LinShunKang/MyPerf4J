package cn.perf4j;

/**
 * Created by LinShunkang on 2018/3/11
 */
public class Record {

    private int time;

    private int count;

    private Record(int time, int count) {
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

    public void reset(Record record) {
        this.time = record.getTime();
        this.count = record.getCount();
    }

    @Override
    public String toString() {
        return "Record{" +
                "time=" + time +
                ", count=" + count +
                '}';
    }

    public static Record getInstance() {
        return getInstance(-1, -1);
    }

    public static Record getInstance(int time, int count) {
        return new Record(time, count);
    }
}
