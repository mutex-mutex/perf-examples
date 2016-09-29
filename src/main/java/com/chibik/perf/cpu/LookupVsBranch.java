package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class LookupVsBranch {

    private ThreadLocalRandom rand = ThreadLocalRandom.current();

    private float[] table = new float[]{1f, 2f};

    @Benchmark
    public float testBranch() {
        int r = rand.nextInt(2);
        if(r == 0) {
            return 1f;
        } else {
            return 2f;
        }
    }

    @Benchmark
    public float testLookup() {
        int r = rand.nextInt(2);
        return table[r];
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(LookupVsBranch.class);
    }
}
