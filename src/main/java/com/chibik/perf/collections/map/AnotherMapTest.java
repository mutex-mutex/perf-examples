package com.chibik.perf.collections.map;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = AnotherMapTest.BATCH_SIZE, iterations = 20, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(batchSize = AnotherMapTest.BATCH_SIZE, iterations = 20, timeUnit = TimeUnit.MICROSECONDS)
public class AnotherMapTest {

    public static final int BATCH_SIZE = 1000000;

    private ConcurrentMap<Long, Long> concurrentMap;

    private Map<Long, Long> hashMap;

    private long[] data = new long[1000000];
    private int index;

    {
        Random r = new Random(30L);
        for(int i = 0; i < data.length; i++) {
            data[i] = r.nextInt(50000000);
        }
    }

    @Setup(Level.Iteration)
    public void setUp() {
        index = 0;

        concurrentMap = new ConcurrentHashMap<>(1000000);
        hashMap = new HashMap<>(1000000);
    }

    @Benchmark
    public void testPutConcurrentHashMap() {
        concurrentMap.put(data[index++], 1L);
    }

    @Benchmark
    public void testPutHashMap() {
        hashMap.put(data[index++], 1L);
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(
                AnotherMapTest.class,
                TimeUnit.MILLISECONDS
        );
    }
}
