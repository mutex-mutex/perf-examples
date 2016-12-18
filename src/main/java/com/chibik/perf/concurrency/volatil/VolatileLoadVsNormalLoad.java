package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 40, batchSize = VolatileLoadVsNormalLoad.BATCH_SIZE)
@Measurement(iterations = 40, batchSize = VolatileLoadVsNormalLoad.BATCH_SIZE)
public class VolatileLoadVsNormalLoad {

    public static final int BATCH_SIZE = 10_000_000;

    @Contended
    private volatile long volatil;

    @Contended
    private long normal;

    @Setup(Level.Iteration)
    public void setUp() {
        volatil = 0xff;
        normal = 0xf;
    }

    @Benchmark
    public long loadVolatile() {
        return volatil;
    }

    @Benchmark
    public long loadNormal() {
        return normal;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(VolatileLoadVsNormalLoad.class);
    }

}
