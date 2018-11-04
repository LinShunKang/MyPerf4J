package MyPerf4J;

import cn.myperf4j.base.util.ThreadUtils;
import org.junit.Test;
import sun.misc.JavaNioAccess;
import sun.misc.SharedSecrets;
import sun.misc.VM;

import java.lang.management.*;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by LinShunkang on 2018/10/31
 */
public class JVMMemoryTest {

    @Test
    public void test() {
        System.out.println("VM.maxDirectMemory=" + VM.maxDirectMemory() / 1024 / 1024 + "MB");
        System.out.println("VM.maxDirectMemory=" + VM.maxDirectMemory());
        printCurMem("Init");

        allocate(1024 * 1024);
        printCurMem("First allocate");

        ByteBuffer buffer = ByteBuffer.allocateDirect(1024 * 1024 * 1024);
        printCurMem("Second allocate");


        System.gc();
        ThreadUtils.sleepQuietly(3, TimeUnit.SECONDS);

        printCurMem("After gc");

        allocate(1024 * 1024 * 1024);
        printCurMem("Third allocate");

        allocate(1024 * 1024);
        allocate(0);
        allocate(0);
        allocate(0);
        allocate(0);

        printCurMem("Fourth allocate");

    }

    private void printCurMem(String desc) {
        JavaNioAccess.BufferPool bufferPool = SharedSecrets.getJavaNioAccess().getDirectBufferPool();
        System.out.println(desc + ": name=" + bufferPool.getName() + " count=" + bufferPool.getCount() + " memoryUsed=" + bufferPool.getMemoryUsed() + " totalCapacity=" + bufferPool.getTotalCapacity());

        MemoryMXBean memoryMXBean = ManagementFactory.getMemoryMXBean();
        MemoryUsage nonHeapMem = memoryMXBean.getNonHeapMemoryUsage();
        System.out.println(desc + ": init=" + nonHeapMem.getInit() + ", max=" + nonHeapMem.getMax() + ", used=" + nonHeapMem.getUsed());

        System.out.println();
    }

    private void allocate(int capacity) {
        ByteBuffer.allocateDirect(capacity);
    }


    @Test
    public void test1() {
        allocate(1024 * 1024);

        List<BufferPoolMXBean> pools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
        for (BufferPoolMXBean mxBean : pools) {
            System.out.println(mxBean.getName() + ", " + mxBean.getCount() + ", " + mxBean.getMemoryUsed() + ", " + mxBean.getTotalCapacity() + ", " + mxBean.getObjectName());
        }

        printCurMem("");
    }

    @Test
    public void test2() {
        CompilationMXBean mxBean = ManagementFactory.getCompilationMXBean();
        System.out.println(mxBean.getName() + ", " + mxBean.getTotalCompilationTime() + ", " + mxBean.isCompilationTimeMonitoringSupported());
    }
}
