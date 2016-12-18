package com.chibik.perf.cpu;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 100, batchSize = TestPipelineStalls.BATCH_SIZE)
@Measurement(iterations = 100, batchSize = TestPipelineStalls.BATCH_SIZE)
public class TestPipelineStalls {

    public static final int BATCH_SIZE = 100000;

    private int a;
    private int b;
    private int c;
    private int d;
    private int e;
    private int f;
    private int g;

    /*
  0x00007f928122dd87: mov    %rbp,0x10(%rsp)    ;*synchronization entry
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithDependencies@-1 (line 26)

  0x00007f928122dd8c: movl   $0x1,0xc(%rsi)     ;*putfield a
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithDependencies@2 (line 26)

  0x00007f928122dd93: movl   $0x2,0x10(%rsi)    ;*putfield b
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithDependencies@12 (line 27)

  0x00007f928122dd9a: movl   $0x4,0x18(%rsi)    ;*putfield d
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithDependencies@32 (line 29)

  0x00007f928122dda1: movl   $0x3,0x14(%rsi)    ;*putfield c
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithDependencies@22 (line 28)

  0x00007f928122dda8: mov    $0x4,%eax
  0x00007f928122ddad: add    $0x10,%rsp
  0x00007f928122ddb1: pop    %rbp
  0x00007f928122ddb2: test   %eax,0x16ed8248(%rip)        # 0x00007f9298106000
                                                ;   {poll_return}
  0x00007f928122ddb8: retq
    */
    @Benchmark
    public int computeWithDependencies() {
        a = 1;
        b = a + 1;
        c = b + 1;
        d = c + 1;
        return d;
    }

    /*
  0x00007f92812d13b8: je     0x00007f92812d13e9  ;*aload_0
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@0 (line 35)

  0x00007f92812d13be: movl   $0x1,0xc(%rsi)     ;*putfield a
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@2 (line 35)

  0x00007f92812d13c5: mov    0x10(%rsi),%eax    ;*getfield b
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@7 (line 36)

  0x00007f92812d13c8: inc    %eax
  0x00007f92812d13ca: mov    %eax,0x1c(%rsi)    ;*putfield e
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@12 (line 36)

  0x00007f92812d13cd: mov    0x14(%rsi),%eax    ;*getfield c
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@17 (line 37)

  0x00007f92812d13d0: inc    %eax
  0x00007f92812d13d2: mov    %eax,0x20(%rsi)    ;*putfield f
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@22 (line 37)

  0x00007f92812d13d5: mov    0x18(%rsi),%eax    ;*getfield d
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@27 (line 38)

  0x00007f92812d13d8: inc    %eax
  0x00007f92812d13da: mov    %eax,0x24(%rsi)    ;*putfield g
                                                ; - com.chibik.perf.cpu.TestPipelineStalls::computeWithNoDependencies@32 (line 38)

  0x00007f92812d13dd: add    $0x30,%rsp
  0x00007f92812d13e1: pop    %rbp
  0x00007f92812d13e2: test   %eax,0x16e34d18(%rip)        # 0x00007f9298106100
                                                ;   {poll_return}
  0x00007f92812d13e8: retq
    */
    @Benchmark
    public int computeWithNoDependencies() {
        a = 1;
        e = b + 1;
        f = c + 1;
        g = d + 1;
        return g;
    }

    /*
Benchmark                             Mode  Cnt       Score       Error  Units
TestPipelineStalls.computeWithDependencies           ss  100  436155.630 ± 87751.548  ns/op
TestPipelineStalls.computeWithDependencies:·asm      ss              NaN                ---
TestPipelineStalls.computeWithNoDependencies         ss  100  353107.490 ± 30018.899  ns/op
TestPipelineStalls.computeWithNoDependencies:·asm    ss              NaN                ---
com.chibik.perf.cpu.TestPipelineStalls.computeWithDependencies/, time=436155.630000, avg=4.4 ns/op, thrput=229275958 op/sec
com.chibik.perf.cpu.TestPipelineStalls.computeWithNoDependencies/, time=353107.490000, avg=3.5 ns/op, thrput=283199883 op/sec
    */

    //-XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*TestPipelineStalls.computeWithDependencies  -XX:CompileCommand=print,*TestPipelineStalls.computeWithNoDependencies
    public static void main(String[] args) {

        BenchmarkRunner.runSimple(TestPipelineStalls.class, TimeUnit.NANOSECONDS);
    }
}
