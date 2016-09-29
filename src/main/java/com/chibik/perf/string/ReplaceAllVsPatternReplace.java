package com.chibik.perf.string;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class ReplaceAllVsPatternReplace {

    private final String str = "ABCDEFGH";

    private String result;

    private Pattern pattern = Pattern.compile("DEF");

    private StringBuilder builder = new StringBuilder(1000);

    @TearDown(Level.Iteration)
    public void validate() {
        if(!"ABCDEAGH".equals(result)) {
            throw new RuntimeException(result);
        }
    }

    @Benchmark
    public String testReplaceAll() {
        result = str.replaceAll("DEF", "DEA");
        return result;
    }

    @Benchmark
    public String testReplaceWithPattern() {
        Matcher matcher = pattern.matcher(str);
        result = matcher.replaceAll("DEA");
        return result;
    }

    @Benchmark
    public String testReplaceWithIndexOf() {
        int index = str.indexOf("DEF");
        result = str.substring(0, index) + "DEA" + str.substring(index + 3);
        return result;
    }

    @Benchmark
    public String testReplaceWithIndexOfWithReusabelBuilder() {
        builder.setLength(0);
        int index = str.indexOf("DEF");
        builder.append(str, 0, index);
        builder.append("DEA", 0, 3);
        builder.append(str, index + 3, str.length());
        result = builder.toString();
        return result;
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(ReplaceAllVsPatternReplace.class);
    }
}
