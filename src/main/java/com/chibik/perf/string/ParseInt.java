package com.chibik.perf.string;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

@State(Scope.Benchmark)
public class ParseInt {

    @Param({"2", "2055", "12342345"})
    private String source;

    @Benchmark
    public int parseInt() {
        return Integer.parseInt(source);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(ParseInt.class);
    }
}
