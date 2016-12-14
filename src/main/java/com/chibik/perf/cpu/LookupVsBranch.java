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
    public float int01_testBranch() {
        int r = rand.nextInt(2);
        if(r == 0) {
            return 1f;
        } else {
            return 2f;
        }
    }

    @Benchmark
    public float int01_testLookup() {
        int r = rand.nextInt(2);
        return table[r];
    }

    @Benchmark
    public float boolean_testBranch() {
        boolean r = rand.nextBoolean();
        if(r) {
            return 1f;
        } else {
            return 2f;
        }
    }

    @Benchmark
    public float boolean_testLookup() {
        boolean r = rand.nextBoolean();
        return table[Boolean.compare(true, r)];
    }

    @Benchmark
    public float boolean_testLookup2() {
        boolean r = rand.nextBoolean();
        return table[r ? 1 : 0];
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(LookupVsBranch.class);
    }
}
