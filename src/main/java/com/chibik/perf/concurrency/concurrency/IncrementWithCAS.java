package com.chibik.perf.concurrency.concurrency;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Comment;
import com.chibik.perf.util.Included;
import com.chibik.perf.util.UnsafeTool;
import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 20)
@Measurement(iterations = 30)
@Included
@Comment("Increment value by using CAS loops. Number of threads is 5")
public class IncrementWithCAS {

    public static long VALUE_OFFSET;
    private static Unsafe unsafe = UnsafeTool.getUnsafe();

    static {
        try {
            Field f = IncrementWithCAS.class.getDeclaredField("value");

            VALUE_OFFSET = unsafe.objectFieldOffset(f);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private long value;

    @Setup(Level.Iteration)
    public void setup() {
        value = 0;
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        if(value == 0) {
            throw new RuntimeException("'value' should be positive but was " + value);
        }
    }

    @Benchmark
    @Group("Cas")
    @GroupThreads(value = 5)
    @Comment("Increment using CAS loop")
    public long plainCAS() {
        long var6;
        for(;;) {
            var6 = unsafe.getLongVolatile(this, VALUE_OFFSET);

            if(unsafe.compareAndSwapLong(this, VALUE_OFFSET, var6, var6 + 1)) {
                break;
            }
        }

        return var6;
    }

    @Benchmark
    @Group("CasWithBackoff")
    @GroupThreads(value = 5)
    @Comment("Increment using CAS loop with a constant backoff")
    public long plainCASWithCsBackoff() {
        long var6;
        for(;;) {
            var6 = unsafe.getLongVolatile(this, VALUE_OFFSET);

            if(unsafe.compareAndSwapLong(this, VALUE_OFFSET, var6, var6 + 1)) {
                break;
            } else {
                LockSupport.parkNanos(20);
            }
        }

        return var6;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(IncrementWithCAS.class);
    }
}
