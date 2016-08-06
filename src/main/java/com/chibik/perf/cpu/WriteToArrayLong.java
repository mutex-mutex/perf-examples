package com.chibik.perf.cpu;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 500, batchSize = WriteToArrayLong.BATCH_SIZE)
@Measurement(iterations = 100, batchSize = WriteToArrayLong.BATCH_SIZE)
public class WriteToArrayLong {

    public static final int BATCH_SIZE = 10;
    public static final int ARRAY_LENGTH = 100000;

    private long[] arr = new long[ARRAY_LENGTH];

    @Benchmark
    public void writeSeq() {
        for(int i = 0; i < arr.length; ++i) {
            arr[i] = 0xff00ff00;
        }
    }

    @Benchmark
    public void writeReverseSeq() {
        for(int i = arr.length - 1; i >= 0; --i) {
            arr[i] = 0xff00ff00;
        }
    }

    //-XX:-TieredCompilation -XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*WriteToArrayLong.writeSeq -XX:CompileCommand=print,*WriteToArrayLong.writeReverseSeq
    public static void main(String[] args) {

        RunBenchmark.runNoFork(WriteToArrayLong.class, TimeUnit.NANOSECONDS);
    }
}
