package com.chibik.perf.string;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class StringParseInt {

    @Param({"2", "2055", "12342345"})
    private String source;

    @Benchmark
    public int parseIntAvgTime() {
        return Integer.parseInt(source);
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(StringParseInt.class);
    }
}
