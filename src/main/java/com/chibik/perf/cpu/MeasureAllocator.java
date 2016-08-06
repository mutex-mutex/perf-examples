package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
public class MeasureAllocator {

    @Benchmark
    public Object create() {
        return new Object();
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(MeasureAllocator.class);
    }
}
