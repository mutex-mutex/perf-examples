package com.chibik.perf;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

//-XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*EmptyAsm.load -XX:-TieredCompilation -XX:BiasedLockingStartupDelay=0 -server

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, batchSize = EmptyAsm.BATCH_SIZE)
@Measurement(iterations = 10, batchSize = EmptyAsm.BATCH_SIZE)
public class EmptyAsm {

    public static final int BATCH_SIZE = 10_000_000;

    @Setup(Level.Iteration)
    public void setUp() {

    }

    @Benchmark
    public long load() {
        return 0;
    }

    public static void main(String[] args) {
        RunBenchmark.runNoFork(EmptyAsm.class, TimeUnit.MILLISECONDS);
    }

}
