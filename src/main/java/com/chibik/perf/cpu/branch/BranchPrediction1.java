package com.chibik.perf.cpu.branch;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class BranchPrediction1 {

    int size = 32768;

    @Contended
    private int[] constant = new int[size];

    @Contended
    private int[] every5 = new int[size];

    @Contended
    private int[] every10 = new int[size];

    @Contended
    private int[] rand = new int[size];

    private int sum;

    @Setup(Level.Iteration)
    public void setUp() {
        for(int i = 0; i < size; i++) {
            constant[i] = (i % 1000 == 0) ? 1 : 0;

            every5[i] = (i % 5 == 0) ? 1 : 0;

            every10[i] = (i % 10 == 0) ? 1 : 0;

            rand[i] = ThreadLocalRandom.current().nextInt(2);
        }
    }

    @Benchmark
    public void constantBranch() {
        for(int i = 0; i < size; i++) {
            if(constant[i] == 0) {
                sum++;
            } else {
                sum--;
            }
        }
    }

    @Benchmark
    public void every5Branch() {
        for(int i = 0; i < size; i++) {
            if(every5[i] == 0) {
                sum++;
            } else {
                sum--;
            }
        }
    }

    @Benchmark
    public void every10Branch() {
        for(int i = 0; i < size; i++) {
            if(every10[i] == 0) {
                sum++;
            } else {
                sum--;
            }
        }
    }

    @Benchmark
    public void randBranch() {
        for(int i = 0; i < size; i++) {
            if(rand[i] == 1) {
                sum++;
            } else {
                sum--;
            }
        }
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(BranchPrediction1.class);
    }
}
