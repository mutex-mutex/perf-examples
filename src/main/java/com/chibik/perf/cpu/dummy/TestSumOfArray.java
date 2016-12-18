package com.chibik.perf.cpu.dummy;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class TestSumOfArray {

    @Param({ "32768" })
    int size;

    @Contended
    private int[] array;

    private int sum;

    @Setup(Level.Iteration)
    public void setUp() {
        array = new int[size];
        for(int i = 0; i < size; i++) {
            array[i] = i % 751;
        }
    }

    @Setup(Level.Invocation)
    public void setUpInv() {
        sum = 0;
    }

    @TearDown(Level.Invocation)
    public void tearDownInv() {
        if(sum != 12222450) {
            throw new RuntimeException(
                    "" + sum
            );
        }
    }

    @Benchmark
    public void addToField() {
        for(int i = 0; i < size; i++) {
            sum += array[i];
        }
    }

    @Benchmark
    public void addWithStack() {
        int s = 0;
        for(int i = 0; i < size; i++) {
            s += array[i];
        }
        sum += s;
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(TestSumOfArray.class);
    }
}
