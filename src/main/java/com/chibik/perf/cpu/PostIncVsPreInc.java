package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 10, batchSize = 1024 * 1024 - 1)
@Measurement(iterations = 30, batchSize = 1024 * 1024 - 1)
@BenchmarkMode(Mode.SingleShotTime)
@State(Scope.Benchmark)
public class PostIncVsPreInc {

    private int[] array = new int[1024 * 1024];

    private int k;

    @Setup(Level.Iteration)
    public void setUp() {
        for(int i = 0; i < array.length; i++) {
            array[i] = i;
        }
        k = 0;
    }

    @Benchmark
    public int getWithPostInc() {
        return array[k++];
    }

    @Benchmark
    public int getWithPreInc() {
        return array[++k];
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(PostIncVsPreInc.class);
    }
}
