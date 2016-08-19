package com.chibik.perf.cpu.dummy;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class TestStoreToObject {

    public static class TestClass {

        public int r1;
        public int r2;
        public int r3;
        public int r4;
        public int r5;
        public int r6;
        public int r7;
        public int r8;
        public int r9;
        public int r10;
        public int r11;
        public int r12;
        public int r13;
        public int r14;
        public int r15;
        public int r16;
        public int r17;
        public int r18;
        public int r19;
        public int r20;
    }

    private TestClass tc;

    @Setup(Level.Iteration)
    public void setUp() {
        tc = new TestClass();
    }

    @Benchmark
    public void storeToA() {
        tc.r1 = 0xf000f000;
    }

    @Benchmark
    public void storeToB() {
        tc.r2 = 0xf000f000;
    }

    @Benchmark
    public void storeToC() {
        tc.r20 = 0xf000f000;
    }


    public static void main(String[] args) {

        RunBenchmark.runSimple(TestStoreToObject.class);
    }
}
