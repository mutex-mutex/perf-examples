package com.chibik.perf.string;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 5, timeUnit = TimeUnit.NANOSECONDS)
@Measurement(iterations = 5, timeUnit = TimeUnit.NANOSECONDS)
public class StringGetBytes {

    @Param({"1", "10", "1000"})
    private int stringLength;

    private String testString;

    @Setup(Level.Iteration)
    public void setUp() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < stringLength; i++) {
            builder.append("a");
            if(i % 10 == 0) {
                builder.append(";");
            }
        }
        this.testString = builder.toString();
    }

    @Benchmark
    public byte[] testGetBytes() {

        return testString.getBytes();
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(StringGetBytes.class);
    }
}
