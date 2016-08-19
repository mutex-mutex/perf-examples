package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
public class MeasureAllocator {

    @Benchmark
    public Object createObject() {
        return new Object();
    }

    @Benchmark
    public Object createArray10() {
        return new int[10];
    }

    @Benchmark
    public Object createArray500() {
        return new int[500];
    }

    @Benchmark
    public Object createArray1000() {
        return new int[1000];
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(MeasureAllocator.class);
    }
}
