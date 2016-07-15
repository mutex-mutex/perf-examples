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
public class HasMapVsConcurrentHashMapPutSingleThreaded {

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
        index = 0;

        concurrentMap = new ConcurrentHashMap<>(1000000);
        hashMap = new HashMap<>(1000000);
        tLongLongHashMap = new TLongLongHashMap();
    }

    @TearDown(Level.Iteration)
    public void end() {
        int finalSize = concurrentMap.size() + hashMap.size() + tLongLongHashMap.size();
        assert finalSize == 1000000;
    }

    @Benchmark
    public void testPutConcurrentHashMap() {
        concurrentMap.put(data[index++], 1L);
    }

    @Benchmark
    public void testPutHashMap() {
        hashMap.put(data[index++], 1L);
    }

    @Benchmark
    public void testPutTLongLongHashMap() {
        tLongLongHashMap.put(data[index++], 1L);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(HasMapVsConcurrentHashMapPutSingleThreaded.class);
    }
}
