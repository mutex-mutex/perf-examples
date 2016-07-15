package com.chibik.perf.collections.map;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
public class HasMapVsConcurrentHashMapGetSingleThreaded {

    private ConcurrentMap<Long, Long> concurrentMap = new ConcurrentHashMap<>();

    private Map<Long, Long> hashMap = new HashMap<>();

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
        for(int i = 0; i < 1000000; i++) {
            concurrentMap.put(data[i], 1L);
        }
    }

    @Benchmark
    public Object testGetConcurrentHashMap() {
        return concurrentMap.get(data[index++]);
    }

    @Benchmark
    public Object testGetHashMap() {
        return hashMap.get(data[index++]);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(HasMapVsConcurrentHashMapGetSingleThreaded.class);
    }
}
