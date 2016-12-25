package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.UnsafeTool;
import com.chibik.perf.util.Comment;
import com.chibik.perf.util.Included;
import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, batchSize = CasVsIncrement.BATCH_SIZE)
@Measurement(iterations = 10, batchSize = CasVsIncrement.BATCH_SIZE)
@Included
public class CasVsIncrement {

    public static final int BATCH_SIZE = 10_000_000;
    public static long VALUE_OFFSET;
    private static Unsafe unsafe = UnsafeTool.getUnsafe();

    static {
        try {
            Field f = AtomicLong.class.getDeclaredField("value");

            VALUE_OFFSET = unsafe.objectFieldOffset(f);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    private AtomicLong counter = new AtomicLong(0);

    @Setup(Level.Iteration)
    public void setUp() {
        counter = new AtomicLong(0);
    }

    @Benchmark
    @Comment("Increment using AtomicLong.incrementAndGet() that uses lock xadd on x86. Uses a single thread")
    public long incrementThroughLockXadd() {
        return counter.incrementAndGet();
    }

    @Benchmark
    @Comment("Increment using CAS loop through Unsafe. Uses a single thread")
    public long plainCas() {
        long var6;
        do {
            var6 = unsafe.getLongVolatile(counter, VALUE_OFFSET);
        } while(!unsafe.compareAndSwapLong(counter, VALUE_OFFSET, var6, var6 + 1));

        return var6;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runNoFork(CasVsIncrement.class, TimeUnit.MILLISECONDS);
    }

}
