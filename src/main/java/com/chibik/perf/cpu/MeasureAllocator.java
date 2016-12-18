package com.chibik.perf.cpu;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

@Warmup(iterations = 5)
@Measurement(iterations = 5)
@BenchmarkMode(Mode.AverageTime)
public class MeasureAllocator {

    @Benchmark
    public Object createObject() {
        return new Object();
    }

    @Benchmark
    public Object createAlloca1() {
        return new AllocaClass1();
    }

    @Benchmark
    public Object createAlloca2() {
        return new AllocaClass2();
    }

    @Benchmark
    public Object createAlloca3() {
        return new AllocaClass3();
    }

    @Benchmark
    public Object createAlloca4() {
        return new AllocaClass4();
    }

    @Benchmark
    public Object createArray10() {
        return new int[10];
    }

    @Benchmark
    public Object createArray500() {
        return new int[500];
    }

    @Benchmark
    public Object createArray1000() {
        return new int[1000];
    }

    public static class AllocaClass1 {
        private String s1;
        private String s2;
        private String s3;
    }

    public static class AllocaClass2 extends AllocaClass1 {
        private String s4;
        private String s5;
        private String s6;
    }

    public static class AllocaClass3 extends AllocaClass2 {
        private String s7;
        private String s8;
        private String s9;
    }

    public static class AllocaClass4 extends AllocaClass3 {
        private String s10;
        private String s11;
        private String s12;
    }

    public static class AllocaClass5 extends AllocaClass4 {
        private String s13;
        private String s14;
        private String s15;
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(MeasureAllocator.class);
    }
}
