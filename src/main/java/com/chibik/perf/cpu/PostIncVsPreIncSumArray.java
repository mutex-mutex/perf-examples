package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class PostIncVsPreIncSumArray {

    private int[] array = new int[1024 * 1024];

    @Setup(Level.Iteration)
    public void setUp() {
        for(int i = 0; i < array.length; i++) {
            array[i] = i & (1024 - 1);
        }
    }

    @Benchmark
    public int getWithPostInc() {
        int sum = 0;
        for(int i = 0; i < array.length;) {
            sum += array[i++];
        }
        return sum;
    }

    @Benchmark
    public int getWithPreInc() {
        int sum = 0;
        for(int i = -1; i < array.length - 1;) {
            sum += array[++i];
        }
        return sum;
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(PostIncVsPreIncSumArray.class);
    }
}
