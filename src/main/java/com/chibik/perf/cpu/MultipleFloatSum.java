package com.chibik.perf.cpu;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
@State(Scope.Benchmark)
public class MultipleFloatSum {

    private float a = 2f;
    private float b = 3f;
    private float c = 4f;
    private float d = 5f;

    @Benchmark
    public float simpleSum() {
        return (((a + b) + c) + d);
    }

    @Benchmark
    public float parallelSum() {
        return (a + b) + (c + d);
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(MultipleFloatSum.class);
    }
}
