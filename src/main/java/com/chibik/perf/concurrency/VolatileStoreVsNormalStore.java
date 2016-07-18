package com.chibik.perf.concurrency;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
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
        RunBenchmark.runSimple(VolatileStoreVsNormalStore.class);
    }

}
