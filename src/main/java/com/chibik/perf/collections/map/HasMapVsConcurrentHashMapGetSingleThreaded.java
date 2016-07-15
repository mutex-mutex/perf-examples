package com.chibik.perf.collections.map;

import com.chibik.perf.RunBenchmark;
import gnu.trove.map.hash.TLongLongHashMap;
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
public class HasMapVsConcurrentHashMapGetSingleThreaded {

    private ConcurrentMap<Long, Long> concurrentMap;

    private Map<Long, Long> hashMap;

    private TLongLongHashMap tLongLongHashMap;

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
        concurrentMap = new ConcurrentHashMap<>();
        hashMap = new HashMap<>();
        tLongLongHashMap = new TLongLongHashMap();

        index = 0;
        for(int i = 0; i < 1000000; i++) {
            concurrentMap.put(data[i], 1L);
            hashMap.put(data[i], 1L);
            tLongLongHashMap.put(data[i], 1L);
        }
    }

    @TearDown(Level.Iteration)
    public void end() {
        int finalSize = concurrentMap.size() + hashMap.size() + tLongLongHashMap.size();
        assert finalSize == 1000000;
    }

    @Benchmark
    public long testGetConcurrentHashMap() {
        return concurrentMap.get(data[index++]);
    }

    @Benchmark
    public long testGetHashMap() {
        return hashMap.get(data[index++]);
    }

    @Benchmark
    public long testGetTLongLongHashMap() {
        return tLongLongHashMap.get(data[index++]);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(HasMapVsConcurrentHashMapGetSingleThreaded.class);
    }
}
