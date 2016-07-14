package com.chibik.perf;

import com.chibik.perf.string.IsEmptyVsEqualsEmpty;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

public class RunBenchmark {

    public static void runSimple(Class<?> clazz) {
        runSimple(clazz, TimeUnit.NANOSECONDS);
    }

    public static void runSimple(Class<?> clazz, TimeUnit timeUnit) {
        try {
            Options opt = new OptionsBuilder()
                    .include(clazz.getSimpleName())
                    .warmupIterations(10)
                    .measurementIterations(20)
                    .jvmArgsAppend(
                            "-Xmx2G",
                            "-XX:BiasedLockingStartupDelay=0",
                            "-server",
                            "-XX:-TieredCompilation",
                            "-XX:+PrintSafepointStatistics"
                    )
                    .forks(1)
                    .build();

            new Runner(opt).run();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
