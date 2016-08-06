package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 1000, batchSize = WriteToArraySixTimes.BATCH_SIZE)
@Measurement(iterations = 100, batchSize = WriteToArraySixTimes.BATCH_SIZE)
public class WriteToArraySixTimes {

    public static final int BATCH_SIZE = 100000;

    private long[] arr = new long[256];

    @Benchmark
    public void writeSeq() {
        arr[0] = 0x0000000f;
        arr[1] = 0x000000f0;
        arr[2] = 0x00000f00;
        arr[3] = 0x0000f000;
        arr[4] = 0x000f0000;
        arr[5] = 0x00f00000;
        arr[6] = 0x0f000000;
    }


    @Benchmark
    public void writeReverseSeq() {
        arr[6] = 0x0f000000;
        arr[5] = 0x00f00000;
        arr[4] = 0x000f0000;
        arr[3] = 0x0000f000;
        arr[2] = 0x00000f00;
        arr[1] = 0x000000f0;
        arr[0] = 0x0000000f;
    }

    @Benchmark
    public void writeNonPredictableStyle() {
        arr[3] = 0x0000f000;
        arr[6] = 0x0f000000;
        arr[0] = 0x0000000f;
        arr[5] = 0x00f00000;
        arr[1] = 0x000000f0;
        arr[2] = 0x00000f00;
        arr[4] = 0x000f0000;
    }

    // -XX:-TieredCompilation -XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*WriteToArraySixTimes.writeSeq -XX:CompileCommand=print,*WriteToArraySixTimes.writeReverseSeq -XX:CompileCommand=print,*WriteToArraySixTimes.writeNonPredictableStyle
    public static void main(String[] args) {

        RunBenchmark.runNoFork(WriteToArraySixTimes.class, TimeUnit.NANOSECONDS);
    }
}
