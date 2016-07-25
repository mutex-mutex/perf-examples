package com.chibik.perf.gc;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

//TODO: not working
@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 4)
@Measurement(iterations = 4)
public class TestStoreBarrier {

    private TestEntity entity;

    @Setup(Level.Trial)
    public void init() {

        entity = new TestEntity();

        System.gc();
        System.gc();
        System.gc();

    }

    @Fork(value = 1, jvmArgs = {"-XX:+NeverTenure", "-XX:+UseConcMarkSweepGC"})
    @Benchmark
    public void updateFromEden() {

        entity.s1 = new Object();
    }

    @Fork(value = 1, jvmArgs = {"-XX:+AlwaysTenure", "-XX:+UseConcMarkSweepGC"})
    @Benchmark
    public void updateFromOldGen() {

        entity.s1 = new Object();
    }

    public static class TestEntity {
        public Object s1;
    }

    //-XX:+UnlockDiagnosticVMOptions -XX:+PrintAssembly -XX:CompileCommand=print,*TestStoreBarrier.updateFromEden
    public static void main(String[] args) {
        RunBenchmark.runSimple(TestStoreBarrier.class, TimeUnit.NANOSECONDS);
    }
}
