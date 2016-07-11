package com.chibik.perf.string;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;

@State(Scope.Benchmark)
@Threads(value = 1)
public class IsEmptyVsEqualsEmpty {

    @Param({"", "a", "abcdef"})
    private String testString;

    /*
    * isEmpty() twice faster
    * */
    @Benchmark
    public boolean isEmpty() {
        return testString.isEmpty();
    }

    @Benchmark
    public boolean equalsEmptyInternedString() {
        return "".equals(testString);
    }

    public static void main(String[] args) throws RunnerException {
        RunBenchmark.runSimple(IsEmptyVsEqualsEmpty.class);
    }
}
