package cn.myperf4j.base.metric;

/**
 * Created by LinShunkang on 2019/11/03
 */
public class JvmCompilationMetrics extends Metrics {

    private static final long serialVersionUID = 4067660607211597333L;

    private final long time; //UNIT: ms

    private final long totalTime; //UNIT: ms

    public JvmCompilationMetrics(long time, long totalTime) {
        this.time = time;
        this.totalTime = totalTime;
    }

    public long getTime() {
        return time;
    }

    public long getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return "JvmCompilationMetrics{" +
                "time=" + time +
                ", time=" + time +
                "} " + super.toString();
    }
}
