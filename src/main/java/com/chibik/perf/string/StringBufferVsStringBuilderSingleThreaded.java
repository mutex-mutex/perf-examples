package com.chibik.perf.string;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = 500000, iterations = 500)
@Measurement(batchSize = 500000, iterations = 500)
public class StringBufferVsStringBuilderSingleThreaded {

    private StringBuilder builder = new StringBuilder(10000000);

    private StringBuffer buffer = new StringBuffer(10000000);

    @Param({"a", "aaaaa"})
    private String addition;

    @Setup(Level.Iteration)
    public void setUp() {
        builder.setLength(0);
        buffer.setLength(0);
    }

    @Benchmark
    public void testBuilderAppend() {
        builder.append('a');
    }

    @Benchmark
    public void testBufferAppend() {
        buffer.append('a');
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringBufferVsStringBuilderSingleThreaded.class.getSimpleName())
                .warmupIterations(3)
                .warmupBatchSize(500000)
                .measurementBatchSize(500000)
                .measurementIterations(500)
                .threads(1)
                .jvmArgsAppend(
                        "-Xmx2G",
                        "-XX:BiasedLockingStartupDelay=0",
                        "-server",
                        "-XX:-TieredCompilation",
                        "-XX:+PrintSafepointStatistics"
                )
                .forks(1)
                .mode(Mode.SingleShotTime)
                .timeUnit(TimeUnit.MICROSECONDS)
                .build();

        new Runner(opt).run();
    }
}
