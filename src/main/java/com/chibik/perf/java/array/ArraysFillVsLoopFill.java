package com.chibik.perf.java.array;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class ArraysFillVsLoopFill {

    @Param({ "1", "10", "100", "1000" })
    private int length;

    @Contended
    private int[] intArray;

    @Setup(Level.Iteration)
    public void setUp() {
        intArray = new int[length];
        for(int i = 0; i < length; i++) {
            intArray[i] = i % 751;
        }
    }

    @Benchmark
    public void arraysFill() {
        Arrays.fill(intArray, 0xdeadbeef);
    }

    @Benchmark
    public void loopFill() {
        for(int i = 0; i < length; i++) {
            intArray[i] = 0xdeadbeef;
        }
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(ArraysFillVsLoopFill.class, TimeUnit.NANOSECONDS);
    }
}
