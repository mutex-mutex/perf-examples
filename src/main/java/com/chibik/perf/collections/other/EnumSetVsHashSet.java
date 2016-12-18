package com.chibik.perf.collections.other;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 40, batchSize = 100000)
@Measurement(iterations = 10, batchSize = 100000)
public class EnumSetVsHashSet {

    private EnumSet<TestEnum> enumSet;

    private HashSet<TestEnum> hashSet;

    @Setup(Level.Invocation)
    public void setUp() {
        enumSet = EnumSet.noneOf(TestEnum.class);
        hashSet = new HashSet<>();
    }

    @Benchmark
    public void putToEnumSet() {
        enumSet.add(TestEnum.A20);
        enumSet.add(TestEnum.A4);
        enumSet.add(TestEnum.A12);
        enumSet.add(TestEnum.A18);
        enumSet.add(TestEnum.A1);
        enumSet.add(TestEnum.A4);
    }

    @Benchmark
    public void putToHashSet() {
        hashSet.add(TestEnum.A20);
        hashSet.add(TestEnum.A4);
        hashSet.add(TestEnum.A12);
        hashSet.add(TestEnum.A18);
        hashSet.add(TestEnum.A1);
        hashSet.add(TestEnum.A4);
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(EnumSetVsHashSet.class, TimeUnit.MICROSECONDS);
    }

    public enum TestEnum {
        A1,
        A2,
        A3,
        A4,
        A5,
        A6,
        A7,
        A8,
        A9,
        A10,
        A11,
        A12,
        A13,
        A14,
        A15,
        A16,
        A17,
        A18,
        A19,
        A20
    }
}
