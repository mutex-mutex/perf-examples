package com.chibik.perf.concurrency;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class ThreadYield {

    @Benchmark
    public void yield() {
        Thread.yield();
    }

    @Benchmark
    public void empty() {

    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(ThreadYield.class);
    }
}
