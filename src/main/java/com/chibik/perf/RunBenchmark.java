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
                    .forks(1)
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
                System.out.println(
                        String.format(
                                "%s/%s, time=%f, avg=%.1f %s, thrput=%.0f op/sec",
                                params.getBenchmark(),
                                printParams(params),
                                result.getPrimaryResult().getScore(),
                                (result.getPrimaryResult().getScore() / getBatchSize(clazz)),
                                result.getPrimaryResult().getScoreUnit(),
                                ((getBatchSize(clazz) / result.getPrimaryResult().getScore()) * 1000 * 1000 * 1000)
                        )
                );
            }

        }
    }

    private static String printParams(BenchmarkParams params) {
        StringBuilder builder = new StringBuilder();

        for(String key : params.getParamsKeys()) {
            String val = params.getParam(key);
            builder.append(key + "=" + val + "/");
        }

        if(builder.length() > 0) {
            builder.setLength(builder.length() - 1);
        }
        return builder.toString();
    }

    private static boolean hasBatchSize(Class<?> clazz) {
        try {

            for(Field field : clazz.getDeclaredFields()) {
                if(field.getName().equals("BATCH_SIZE")) {
                    return true;
                }
            }

            return hasBatchSize(clazz.getSuperclass());
        } catch (Exception e) {

            return false;
        }
    }

    private static int getBatchSize(Class<?> clazz) {
        try {

            for(Field field : clazz.getDeclaredFields()) {
                if(field.getName().equals("BATCH_SIZE")) {
                    field.setAccessible(true);
                    return field.getInt(clazz);
                }
            }

            return getBatchSize(clazz.getSuperclass());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {

    }
}
