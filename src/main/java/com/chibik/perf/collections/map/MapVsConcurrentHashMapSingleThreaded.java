package com.chibik.perf.collections.map;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
public class MapVsConcurrentHashMapSingleThreaded {

    private ConcurrentMap<Long, Long> concurrentMap = new ConcurrentHashMap<>();

    private Map<Long, Long> hashMap = new HashMap<>();

    private long[] data;
    private int index;

    @Setup(Level.Iteration)
    public void setUp() {
        index = 0;
        data = new long[1000000];
        Random r = new Random(50L);
        for(int i = 0; i < data.length; i++) {
            data[i] = r.nextInt(50000000);
        }
    }

    @Benchmark
    public Object testConcurrentHashMap() {
        return concurrentMap.put(data[index++], 1L);
    }

    @Benchmark
    public Object testHashMap() {
        return hashMap.put(data[index++], 1L);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(MapVsConcurrentHashMapSingleThreaded.class);
    }
}
