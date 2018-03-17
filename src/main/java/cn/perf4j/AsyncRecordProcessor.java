package cn.perf4j;

import cn.perf4j.util.ThreadUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/3/16
 */
public class AsyncRecordProcessor implements RecordProcessor, InitializingBean {

    private static final ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 2, 5, TimeUnit.MINUTES, new LinkedBlockingQueue<Runnable>(2000), ThreadUtils.newThreadFactory("MyPerf4J-AsyncRecordProcessor_"), new ThreadPoolExecutor.DiscardPolicy());

    private final RecordProcessor target;

    public AsyncRecordProcessor(RecordProcessor target) {
        this.target = target;
    }

    @Override
    public void process(final String api, final long startMilliTime, final long stopMillTime, final List<Record> sortedRecords) {
        try {
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    target.process(api, startMilliTime, stopMillTime, sortedRecords);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(target, "target is required!!!");
    }
}
