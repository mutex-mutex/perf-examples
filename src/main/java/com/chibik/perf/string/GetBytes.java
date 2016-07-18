package com.chibik.perf.string;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
public class GetBytes {

    @Param({"1", "10", "1000"})
    private int strLength;

    private String testString;

    @Setup(Level.Trial)
    public void setUp() {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < strLength; i++) {
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

        RunBenchmark.runSimple(GetBytes.class);
    }
}
