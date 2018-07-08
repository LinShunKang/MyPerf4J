package cn.myperf4j.core;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by LinShunkang on 2018/7/7
 */
public class Recorders {

    private AtomicReferenceArray<Recorder> recorderArr;

    private AtomicInteger recorderCount;

    private volatile boolean writing = false;

    private volatile long startTime;

    private volatile long stopTime;


    public Recorders(AtomicReferenceArray<Recorder> recorderArr) {
        this.recorderArr = recorderArr;
        this.recorderCount = new AtomicInteger(0);
    }

    public Recorder getRecorder(int index) {
        return recorderArr.get(index);
    }

    public void setRecorder(int index, Recorder recorder) {
        recorderArr.set(index, recorder);
        recorderCount.incrementAndGet();
    }

    public int size() {
        return recorderArr.length();
    }

    public boolean isWriting() {
        return writing;
    }

    public void setWriting(boolean writing) {
        this.writing = writing;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStopTime() {
        return stopTime;
    }

    public void setStopTime(long stopTime) {
        this.stopTime = stopTime;
    }

    public void resetRecorder() {
        int count = recorderCount.get();
        for (int i = 0; i < count; ++i) {
            Recorder recorder = recorderArr.get(i);
            if (recorder != null) {
                recorder.resetRecord();
            }
        }
    }

    @Override
    public String toString() {
        return "Recorders{" +
                "recorderArr=" + recorderArr +
                ", writing=" + writing +
                ", startTime=" + startTime +
                ", stopTime=" + stopTime +
                '}';
    }
}
