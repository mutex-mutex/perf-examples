package com.chibik.perf.cpu.dummy;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class TestMethodCall {

    private TestClass64 tc = new TestClass64();

    @Benchmark
    public void bench() {
        tc.foo(0xfff0fff0);
    }

    public class TestClass64 {
        private int a;

        public void foo(int b) {
            a = b;
        }
    }

    //-XX:-TieredCompilation -XX:BiasedLockingStartupDelay=0 -XX:MaxInlineSize=0 -XX:CompileCommand=print,*TestClass64.foo -XX:CompileCommand=print,*TestMethodCall.bench -XX:PrintAssemblyOptions=intel,hsdis-help -XX:+UnlockDiagnosticVMOptions
    public static void main(String[] args) {
        RunBenchmark.runSimple(
                TestMethodCall.class,
                TimeUnit.NANOSECONDS,
                0
        );
    }
}
