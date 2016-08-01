package com.chibik.perf;

import com.chibik.perf.concurrency.support.UnsafeTool;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.profile.LinuxPerfAsmProfiler;
import org.openjdk.jmh.results.RunResult;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RunBenchmark {

    private static Unsafe UNSAFE = UnsafeTool.getUnsafe();

    public static void runSimple(Class<?> clazz) {
        runSimple(clazz, TimeUnit.NANOSECONDS);
    }

    public static void runSimple(Class<?> clazz, TimeUnit timeUnit) {
        runSimple(clazz, timeUnit, new String[]{});
    }

    public static void runSimple(Class<?> clazz, TimeUnit timeUnit, String... jvmArgs) {
        try {
            Options opt = new OptionsBuilder()
                    .include(clazz.getSimpleName())
                    .jvmArgsAppend(
                            "-Xmx4G",
                            "-XX:BiasedLockingStartupDelay=0",
                            "-server",
                            "-XX:-TieredCompilation",
                            "-ea",
                            jvmArgs.length > 0 ? jvmArgs[0] : "-ea"
                    )
                    .addProfiler(LinuxPerfAsmProfiler.class)
                    .timeUnit(timeUnit)
                    .forks(0)
                    .build();

            Collection<RunResult> runResults = new Runner(opt).run();

            printResults(clazz, runResults);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printResults(Class<?> clazz, Collection<RunResult> runResults) {
        for(RunResult result : runResults) {

            BenchmarkParams params = result.getParams();

            if(params.getMode() == Mode.SingleShotTime && hasBatchSize(clazz)) {
                System.out.printf(
                        params.getBenchmark() +
                        ", snapshot time=" + result.getPrimaryResult().getScore() +
                        ", avg=" + (result.getPrimaryResult().getScore() / getBatchSize(clazz)) +
                        " " + result.getPrimaryResult().getScoreUnit()
                );
            }

        }
    }

    private static boolean hasBatchSize(Class<?> clazz) {
        try {

            return clazz.getDeclaredField("BATCH_SIZE") != null;
        } catch (Exception e) {

            return false;
        }
    }

    private static int getBatchSize(Class<?> clazz) {
        try {
            Field field = clazz.getDeclaredField("BATCH_SIZE");
            field.setAccessible(true);
            return field.getInt(clazz);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
//        System.out.println(hasBatchSize(PassingLatencyVolatile.class));
//        System.out.println(getBatchSize(PassingLatencyVolatile.class));
    }
}
