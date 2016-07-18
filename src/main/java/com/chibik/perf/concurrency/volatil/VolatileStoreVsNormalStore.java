package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
public class VolatileStoreVsNormalStore {

    private volatile long volatileField;

    private long normalField;

    @Benchmark
    public void testVolatileStore() {
        volatileField = 2L;
    }

    @Benchmark
    public void testNormalStore() {
        normalField = 2L;
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(VolatileStoreVsNormalStore.class, TimeUnit.NANOSECONDS);
    }

}
