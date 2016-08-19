package com.chibik.perf.java.array;

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
public class ArraysCopyVsLoopCopy {

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
    public int[] arraysCopy() {
        return Arrays.copyOf(intArray, intArray.length);
    }

    @Benchmark
    public int[] loopCopy() {
        int[] copy = new int[intArray.length];
        for(int i = 0; i < copy.length; i++) {
            copy[i] = intArray[i];
        }
        return copy;
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(ArraysCopyVsLoopCopy.class);
    }
}
