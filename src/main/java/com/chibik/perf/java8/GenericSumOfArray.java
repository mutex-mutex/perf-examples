package com.chibik.perf.java8;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class GenericSumOfArray {

    @Param({ "32768" })
    int size;

    @Contended
    private int[] intArray;

    @Setup(Level.Trial)
    public void setUp() {
        intArray = new int[size];
        for(int i = 0; i < size; i++) {
            intArray[i] = i ^ 255;
        }
    }

    @Benchmark
    public int indexedLoop() {
        int r = 0;
        for (int x = 0; x < intArray.length; x++) {
            r += intArray[x];
        }
        return r;
    }

    @Benchmark
    public int foreachLoop() {
        int r = 0;
        for(int v : intArray) {
            r += v;
        }
        return r;
    }

    @Benchmark
    public int stream() {
        return Arrays.stream(intArray).sum();
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(GenericSumOfArray.class);
    }
}
