package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Comment;
import com.chibik.perf.util.Included;
import com.chibik.perf.util.UnsafeTool;
import org.openjdk.jmh.annotations.*;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 10, timeUnit = TimeUnit.SECONDS)
@Warmup(iterations = 10)
//TODO: set iterations count properly
@Included
public class UnsafeVsAtomics {

    private Test1Class test;

    @Setup(Level.Iteration)
    public void setUp() {
        test = new Test1Class();
    }

    @Comment("Unsafe.putBooleanVolatile(..) with 'true' as a value passed")
    @Benchmark
    public void putBooleanVolatile() {
        test.putBooleanVolatile();
    }

    @Comment("AtomicBoolean.set(..) with 'true' as a value passed")
    @Benchmark
    public void atomicBooleanSetVolatile() {
        test.atomicBooleanSetVolatile();
    }

    @Comment("AtomicBoolean.compareAndSet(true, true)")
    @Benchmark
    public void casThroughAtomicBoolean() {
        test.casThroughAtomicBoolean();
    }

    @Comment("AtomicBoolean.weakCompareAndSet(true, true)")
    @Benchmark
    public void weakCasThroughAtomicBoolean() {
        test.weakCasThroughAtomicBoolean();
    }

    private static class Test1Class {

        private boolean plainBoolean = true;
        private AtomicBoolean atomicBoolean = new AtomicBoolean(true);

        private static long PLAIN_BOOLEAN_OFFSET;
        public static final Unsafe unsafe = UnsafeTool.getUnsafe();

        static {
            try {
                Field field = Test1Class.class.getDeclaredField("plainBoolean");
                field.setAccessible(true);
                PLAIN_BOOLEAN_OFFSET = unsafe.objectFieldOffset(field);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        }

        public void putBooleanVolatile() {
            unsafe.putBooleanVolatile(this, PLAIN_BOOLEAN_OFFSET, true);
        }

        public void atomicBooleanSetVolatile() {
            atomicBoolean.set(true);
        }

        public void casThroughAtomicBoolean() {
            atomicBoolean.compareAndSet(true, true);
        }

        public void weakCasThroughAtomicBoolean() {
            atomicBoolean.weakCompareAndSet(true, true);
        }
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(UnsafeVsAtomics.class);
    }
}
