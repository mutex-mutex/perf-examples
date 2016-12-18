package com.chibik.perf.string;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;

import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
public class StringTokenizerVsSplit {

    private String testString;

    {
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < 100000; i++) {
            builder.append("a");
            if(i % 10 == 0) {
                builder.append(";");
            }
        }
        this.testString = builder.toString();
    }

    @Benchmark
    public int testTokenizer() {
        StringTokenizer tokenizer = new StringTokenizer(testString, ";");
        int c = 0;
        while(tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            c += 1;
        }
        return c;
    }

    @Benchmark
    public int split() {
        return testString.split(";").length;
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(StringTokenizerVsSplit.class, TimeUnit.MICROSECONDS);
    }
}
