package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.misc.Unsafe;
import java.util.concurrent.TimeUnit;

import static com.chibik.perf.concurrency.support.UnsafeTool.getUnsafe;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 40, batchSize = VolatileStoreVsPutOrderedSimple.BATCH_SIZE)
@Measurement(iterations = 40, batchSize = VolatileStoreVsPutOrderedSimple.BATCH_SIZE)
public class VolatileStoreVsPutOrderedSimple {

    public static final int BATCH_SIZE = 10_000_000;

    private static Unsafe u = getUnsafe();

    private static long VOLATILE_OFFSET;

    static {
        try {

            VOLATILE_OFFSET = u.objectFieldOffset(TestEntity.class.getDeclaredField("id"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private TestEntity testEntity = new TestEntity();


    /*
....[Hottest Region 2]..............................................................................
 [0x7fb4e509ad68:0x7fb4e509adb5] in com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub

                      0x00007fb4e509ad68: mov    0x10(%rsp),%r10
                      0x00007fb4e509ad6d: movzbl 0x94(%r10),%r10d   ;*getfield isDone
                                                                    ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@24 (line 167)
                                                                    ; implicit exception: dispatches to 0x00007fb4e509ae05
                      0x00007fb4e509ad75: mov    $0x1,%ebp
                      0x00007fb4e509ad7a: test   %r10d,%r10d
                  ╭   0x00007fb4e509ad7d: jne    0x00007fb4e509ada8  ;*ifeq
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@27 (line 167)
                  │   0x00007fb4e509ad7f: nop                       ;*aload_3
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@13 (line 165)
  5.60%    6.49%  │↗  0x00007fb4e509ad80: mov    0x8(%rsp),%rsi
                  ││  0x00007fb4e509ad85: xchg   %ax,%ax
  7.36%    6.54%  ││  0x00007fb4e509ad87: callq  0x00007fb4e5046020  ; OopMap{[0]=Oop [8]=Oop [16]=Oop off=140}
                  ││                                                ;*invokevirtual testPutOrderedLongToVolatile
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@14 (line 165)
                  ││                                                ;   {optimized virtual_call}
 15.06%   15.69%  ││  0x00007fb4e509ad8c: mov    0x10(%rsp),%r10
  5.71%    5.59%  ││  0x00007fb4e509ad91: movzbl 0x94(%r10),%r10d   ;*getfield isDone
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@24 (line 167)
                  ││  0x00007fb4e509ad99: add    $0x1,%rbp          ; OopMap{[0]=Oop [8]=Oop [16]=Oop off=157}
                  ││                                                ;*ifeq
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@27 (line 167)
  7.50%    6.82%  ││  0x00007fb4e509ad9d: test   %eax,0xc37e25d(%rip)        # 0x00007fb4f1419000
                  ││                                                ;   {poll}
                  ││  0x00007fb4e509ada3: test   %r10d,%r10d
                  │╰  0x00007fb4e509ada6: je     0x00007fb4e509ad80  ;*aload_2
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@30 (line 168)
                  ↘   0x00007fb4e509ada8: mov    $0x7fb4f0126ad0,%r10
                      0x00007fb4e509adb2: callq  *%r10              ;*invokestatic nanoTime
                                                                    ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testPutOrderedLongToVolatile_jmhTest::testPutOrderedLongToVolatile_avgt_jmhStub@31 (line 168)
                      0x00007fb4e509adb5: mov    (%rsp),%r10
....................................................................................................
    */
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testPutOrderedLongToVolatile() {

        u.putOrderedLong(testEntity, VOLATILE_OFFSET, 2);
    }




    /*
....[Hottest Region 1]..............................................................................
 [0x7fea050a0c20:0x7fea050a0c7c] in com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore

                     #           [sp+0x20]  (sp of caller)
                     0x00007fea050a0c20: mov    0x8(%rsi),%r10d
                     0x00007fea050a0c24: shl    $0x3,%r10
                     0x00007fea050a0c28: cmp    %r10,%rax
                     0x00007fea050a0c2b: jne    0x00007fea05045e20  ;   {runtime_call}
                     0x00007fea050a0c31: xchg   %ax,%ax
                     0x00007fea050a0c34: nopl   0x0(%rax,%rax,1)
                     0x00007fea050a0c3c: xchg   %ax,%ax
                   [Verified Entry Point]
                     0x00007fea050a0c40: mov    %eax,-0x14000(%rsp)
  2.91%              0x00007fea050a0c47: push   %rbp
                     0x00007fea050a0c48: sub    $0x10,%rsp         ;*synchronization entry
                                                                   ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore@-1 (line 136)
  2.40%              0x00007fea050a0c4c: mov    0xc(%rsi),%r11d    ;*getfield testEntity
                                                                   ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore@1 (line 136)
                     0x00007fea050a0c50: test   %r11d,%r11d
                  ╭  0x00007fea050a0c53: je     0x00007fea050a0c6f
                  │  0x00007fea050a0c55: movq   $0x2,0x10(%r12,%r11,8)
                  │  0x00007fea050a0c5e: lock addl $0x0,(%rsp)     ;*putfield id
                  │                                                ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple$TestEntity::setId@2 (line 204)
                  │                                                ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore@7 (line 136)
 84.58%   98.63%  │  0x00007fea050a0c63: add    $0x10,%rsp
                  │  0x00007fea050a0c67: pop    %rbp
                  │  0x00007fea050a0c68: test   %eax,0xcbad392(%rip)        # 0x00007fea11c4e000
                  │                                                ;   {poll_return}
  2.44%           │  0x00007fea050a0c6e: retq
                  ↘  0x00007fea050a0c6f: mov    $0xfffffff6,%esi
                     0x00007fea050a0c74: xchg   %ax,%ax
                     0x00007fea050a0c77: callq  0x00007fea050051a0  ; OopMap{off=92}
                                                                   ;*invokevirtual setId
                                                                   ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore@7 (line 136)
                                                                   ;   {runtime_call}
                     0x00007fea050a0c7c: callq  0x00007fea1095b0b0  ;*invokevirtual setId
                                                                   ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testVolatileStore@7 (line 136)
                                                                   ;   {runtime_call}
....................................................................................................
    */
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testVolatileStore() {

        testEntity.setId(2);
    }



    /*
    ....[Hottest Region 1]..............................................................................
 [0x7f6c8d09ee60:0x7f6c8d09eeb0] in com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore

                    # {method} {0x00007f6c84ca9d30} &apos;testNormalStore&apos; &apos;()V&apos; in &apos;com/chibik/perf/concurrency/volatil/VolatileStoreVsPutOrderedSimple&apos;
                    #           [sp+0x20]  (sp of caller)
                    0x00007f6c8d09ee60: mov    0x8(%rsi),%r10d
                    0x00007f6c8d09ee64: shl    $0x3,%r10
                    0x00007f6c8d09ee68: cmp    %r10,%rax
                    0x00007f6c8d09ee6b: jne    0x00007f6c8d045e20  ;   {runtime_call}
                    0x00007f6c8d09ee71: xchg   %ax,%ax
                    0x00007f6c8d09ee74: nopl   0x0(%rax,%rax,1)
                    0x00007f6c8d09ee7c: xchg   %ax,%ax
                  [Verified Entry Point]
  2.43%    2.09%    0x00007f6c8d09ee80: mov    %eax,-0x14000(%rsp)
 12.95%   12.89%    0x00007f6c8d09ee87: push   %rbp
  0.58%    0.70%    0x00007f6c8d09ee88: sub    $0x10,%rsp         ;*synchronization entry
                                                                  ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore@-1 (line 186)
  6.12%    5.37%    0x00007f6c8d09ee8c: mov    0xc(%rsi),%r11d    ;*getfield testEntity
                                                                  ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore@1 (line 186)
  9.01%    8.74%    0x00007f6c8d09ee90: movq   $0x2,0x18(%r12,%r11,8)  ;*synchronization entry
                                                                  ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore@-1 (line 186)
                                                                  ; implicit exception: dispatches to 0x00007f6c8d09eea5
  8.16%    7.65%    0x00007f6c8d09ee99: add    $0x10,%rsp
  1.30%    0.55%    0x00007f6c8d09ee9d: pop    %rbp
  4.22%    4.41%    0x00007f6c8d09ee9e: test   %eax,0xc2e515c(%rip)        # 0x00007f6c99384000
                                                                  ;   {poll_return}
  5.11%    5.40%    0x00007f6c8d09eea4: retq
                    0x00007f6c8d09eea5: mov    $0xfffffff6,%esi
                    0x00007f6c8d09eeaa: nop
                    0x00007f6c8d09eeab: callq  0x00007f6c8d0051a0  ; OopMap{off=80}
                                                                  ;*invokevirtual setIdNormal
                                                                  ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore@7 (line 186)
                                                                  ;   {runtime_call}
                    0x00007f6c8d09eeb0: callq  0x00007f6c980910b0  ;*invokevirtual setIdNormal
                                                                  ; - com.chibik.perf.concurrency.volatil.VolatileStoreVsPutOrderedSimple::testNormalStore@7 (line 186)
                                                                  ;   {runtime_call}
....................................................................................................
 49.88%   47.80%  <total for region 1>

....[Hottest Region 2]..............................................................................
 [0x7f6c8d09ace8:0x7f6c8d09ad3d] in com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub

                      0x00007f6c8d09ace8: mov    0x10(%rsp),%r10
                      0x00007f6c8d09aced: movzbl 0x94(%r10),%r10d   ;*getfield isDone
                                                                    ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@24 (line 167)
                                                                    ; implicit exception: dispatches to 0x00007f6c8d09ad85
                      0x00007f6c8d09acf5: mov    $0x1,%ebp
                      0x00007f6c8d09acfa: test   %r10d,%r10d
                  ╭   0x00007f6c8d09acfd: jne    0x00007f6c8d09ad28  ;*ifeq
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@27 (line 167)
                  │   0x00007f6c8d09acff: nop                       ;*aload_3
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@13 (line 165)
  2.04%    2.06%  │↗  0x00007f6c8d09ad00: mov    0x8(%rsp),%rsi
  3.67%    3.77%  ││  0x00007f6c8d09ad05: xchg   %ax,%ax
  8.52%    8.74%  ││  0x00007f6c8d09ad07: callq  0x00007f6c8d046020  ; OopMap{[0]=Oop [8]=Oop [16]=Oop off=140}
                  ││                                                ;*invokevirtual testNormalStore
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@14 (line 165)
                  ││                                                ;   {optimized virtual_call}
 17.31%   19.78%  ││  0x00007f6c8d09ad0c: mov    0x10(%rsp),%r10
  2.74%    2.50%  ││  0x00007f6c8d09ad11: movzbl 0x94(%r10),%r10d   ;*getfield isDone
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@24 (line 167)
  4.20%    4.32%  ││  0x00007f6c8d09ad19: add    $0x1,%rbp          ; OopMap{[0]=Oop [8]=Oop [16]=Oop off=157}
                  ││                                                ;*ifeq
                  ││                                                ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@27 (line 167)
  9.73%    9.74%  ││  0x00007f6c8d09ad1d: test   %eax,0xc2e92dd(%rip)        # 0x00007f6c99384000
                  ││                                                ;   {poll}
  0.67%    0.22%  ││  0x00007f6c8d09ad23: test   %r10d,%r10d
                  │╰  0x00007f6c8d09ad26: je     0x00007f6c8d09ad00  ;*aload_2
                  │                                                 ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@30 (line 168)
                  ↘   0x00007f6c8d09ad28: mov    $0x7f6c98091ad0,%r10
                      0x00007f6c8d09ad32: callq  *%r10              ;*invokestatic nanoTime
                                                                    ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@31 (line 168)
                      0x00007f6c8d09ad35: mov    (%rsp),%r10
                      0x00007f6c8d09ad39: mov    %rbp,0x18(%r10)    ;*putfield measuredOps
                                                                    ; - com.chibik.perf.concurrency.volatil.generated.VolatileStoreVsPutOrderedSimple_testNormalStore_jmhTest::testNormalStore_avgt_jmhStub@46 (line 170)
                      0x00007f6c8d09ad3d: mov    %rax,0x30(%r10)    ;*putfield stopTime
....................................................................................................
    */
    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testNormalStore() {

        testEntity.setIdNormal(2);
    }

    public static void main(String[] args) throws RunnerException {
        RunBenchmark.runSimple(VolatileStoreVsPutOrderedSimple.class, TimeUnit.MICROSECONDS );
    }

    public static class TestEntity {

        private volatile long id;

        private long idNormal;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public long getIdNormal() {
            return idNormal;
        }

        public void setIdNormal(long idNormal) {
            this.idNormal = idNormal;
        }
    }
}
