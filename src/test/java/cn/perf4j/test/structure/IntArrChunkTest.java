package cn.perf4j.test.structure;

import cn.perf4j.util.ChunkPool;

import java.util.Arrays;

/**
 * Created by LinShunkang on 2018/4/6
 */
public class IntArrChunkTest {

    public static void main(String[] args) {
        ChunkPool pool = ChunkPool.getInstance();

        int[] arr = pool.getChunk(1024);
        System.out.println(Arrays.toString(arr));

        Arrays.fill(arr, 1);
        System.out.println(Arrays.toString(arr));

        pool.returnChunk(arr);

        pool.returnChunk(new int[1024]);
        pool.returnChunk(new int[1025]);

        long startNanos = System.nanoTime();
        int times = 100000;
        long hashCodes = 0L;
        for (int i = 0; i < times; ++i) {
//            hashCodes += Arrays.hashCode(arr);
            hashCodes += arr.hashCode();
        }
        System.out.println("hashCodes=" + hashCodes + ", cost: " + (System.nanoTime() - startNanos) / times + " ns per time");

    }
}
