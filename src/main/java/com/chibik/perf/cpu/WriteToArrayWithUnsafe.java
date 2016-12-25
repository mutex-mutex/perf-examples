package com.chibik.perf.cpu;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.UnsafeTool;
import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(iterations = 1000, batchSize = WriteToArrayWithUnsafe.BATCH_SIZE)
@Measurement(iterations = 100, batchSize = WriteToArrayWithUnsafe.BATCH_SIZE)
public class WriteToArrayWithUnsafe {

    public static final int BATCH_SIZE = 100000;

    private static Unsafe UNSAFE = UnsafeTool.getUnsafe();

    private static final int BUFFER_ARRAY_BASE;

    static {
        try  {
            BUFFER_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private long[] buffer = new long[256];

    /*
0x00007fad85352961: mov    $0xfffffffff000f000,%rdi
0x00007fad8535296b: cmpl   $0x0,0xc(%rsi)     ; implicit exception: dispatches to 0x00007fad85352a4b
0x00007fad85352972: jbe    0x00007fad85352a55
0x00007fad85352978: mov    $0xfffffffff000f000,%r10
0x00007fad85352982: mov    %r10,0x10(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@8 (line 20)

0x00007fad85352986: cmpl   $0x1,0xc(%rsi)
0x00007fad8535298d: jbe    0x00007fad85352a62
0x00007fad85352993: mov    $0xfffffffff000f000,%r10
0x00007fad8535299d: mov    %r10,0x18(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@17 (line 21)

0x00007fad853529a1: cmpl   $0x2,0xc(%rsi)
0x00007fad853529a8: jbe    0x00007fad85352a6f
0x00007fad853529ae: mov    $0xfffffffff000f000,%r10
0x00007fad853529b8: mov    %r10,0x20(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@26 (line 22)

0x00007fad853529bc: cmpl   $0x3,0xc(%rsi)
0x00007fad853529c3: jbe    0x00007fad85352a7c
0x00007fad853529c9: mov    $0xfffffffff000f000,%r10
0x00007fad853529d3: mov    %r10,0x28(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@35 (line 23)

0x00007fad853529d7: cmpl   $0x4,0xc(%rsi)
0x00007fad853529de: jbe    0x00007fad85352a89
0x00007fad853529e4: mov    $0xfffffffff000f000,%r10
0x00007fad853529ee: mov    %r10,0x30(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@44 (line 24)

                  cmpl   $0x5,0xc(%rsi)
                  jbe    0x00007fad85352a96
                  mov    $0xfffffffff000f000,%r10
                  mov    %r10,0x38(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@53 (line 25)

0x00007fad85352a0d: cmpl   $0x6,0xc(%rsi)
0x00007fad85352a14: jbe    0x00007fad85352aa3
0x00007fad85352a1a: mov    $0xfffffffff000f000,%r10
0x00007fad85352a24: mov    %r10,0x40(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeSeq@63 (line 26)
* */
    @Benchmark
    public void writeSeq() {
        buffer[0] = 0xf000f000;
        buffer[1] = 0xf000f000;
        buffer[2] = 0xf000f000;
        buffer[3] = 0xf000f000;
        buffer[4] = 0xf000f000;
        buffer[5] = 0xf000f000;
        buffer[6] = 0xf000f000;
    }

    /*
0x00007fad852d1cf8: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d02: mov    %r10,0x40(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@9 (line 31)

0x00007fad852d1d06: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d10: mov    %r10,0x38(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@18 (line 32)

0x00007fad852d1d14: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d1e: mov    %r10,0x30(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@27 (line 33)

0x00007fad852d1d22: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d2c: mov    %r10,0x28(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@36 (line 34)

0x00007fad852d1d30: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d3a: mov    %r10,0x20(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@45 (line 35)

0x00007fad852d1d3e: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d48: mov    %r10,0x18(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@54 (line 36)

0x00007fad852d1d4c: mov    $0xfffffffff000f000,%r10
0x00007fad852d1d56: mov    %r10,0x10(%rsi)    ;*lastore
                                            ; - com.chibik.perf.cpu.WriteToArraySixTimes::writeReverseSeq@63 (line 37)
* */
    @Benchmark
    public void writeReverseSeq() {
        buffer[6] = 0xf000f000;
        buffer[5] = 0xf000f000;
        buffer[4] = 0xf000f000;
        buffer[3] = 0xf000f000;
        buffer[2] = 0xf000f000;
        buffer[1] = 0xf000f000;
        buffer[0] = 0xf000f000;
    }

    @Benchmark
    public void writeSeqWithUnsafe() {
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (0 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (1 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (2 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (3 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (4 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (5 << 3), 0xff00ff00);
        UNSAFE.putLong(buffer, BUFFER_ARRAY_BASE + (6 << 3), 0xff00ff00);
    }

    @Benchmark
    public void putOrderedSeqWithUnsafe() {
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (0 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (1 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (2 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (3 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (4 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (5 << 3), 0xff00ff00);
        UNSAFE.putOrderedLong(buffer, BUFFER_ARRAY_BASE + (6 << 3), 0xff00ff00);
    }

    //-XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*WriteToArrayWithUnsafe.writeSeqWithUnsafe
    public static void main(String[] args) {

        BenchmarkRunner.runNoFork(WriteToArrayWithUnsafe.class, TimeUnit.NANOSECONDS);
    }
}
