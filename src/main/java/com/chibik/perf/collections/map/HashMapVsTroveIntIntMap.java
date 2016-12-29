package com.chibik.perf.collections.map;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Included;
import gnu.trove.map.hash.TIntIntHashMap;
import org.openjdk.jmh.annotations.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(batchSize = 1000000, iterations = 20)
@Measurement(batchSize = 1000000, iterations = 20)
@Included
public class HashMapVsTroveIntIntMap {

    private Map<Integer, Integer> hashMap;

    private TIntIntHashMap tIntIntHashMap;

    private int[] data = new int[1000000];
    private int index;

    @Setup(Level.Iteration)
    public void setUp() {
        Random r = new Random(30L);
        for(int i = 0; i < data.length; i++) {
            data[i] = r.nextInt(50000000);
        }

        index = 0;

        hashMap = new HashMap<>();
        tIntIntHashMap = new TIntIntHashMap();
    }

    @Benchmark
    public void putToHashMap() {
        hashMap.put(data[index], 1);
    }

    @Benchmark
    public void putToTIntIntHashMap() {
        tIntIntHashMap.put(data[index], 1);
    }

    @TearDown(Level.Invocation)
    public void inc() {
        index++;
    }

    @TearDown(Level.Iteration)
    public void validate() {
        int sumSize = tIntIntHashMap.size() + hashMap.size();
        if(sumSize != 990161) {
            throw new RuntimeException("Expected at least one element but was " + sumSize);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(HashMapVsTroveIntIntMap.class, TimeUnit.MILLISECONDS);
    }
}
