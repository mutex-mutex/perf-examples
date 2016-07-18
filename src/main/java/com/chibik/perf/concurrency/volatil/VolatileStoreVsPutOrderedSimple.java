package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.misc.Unsafe;
import java.util.concurrent.TimeUnit;

import static com.chibik.perf.concurrency.support.UnsafeTool.getUnsafe;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class VolatileStoreVsPutOrderedSimple {

    private static Unsafe u = getUnsafe();

    private static long FIELD_OFFSET;

    static {
        try {
            FIELD_OFFSET = u.objectFieldOffset(TestEntity.class.getDeclaredField("id"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private TestEntity testEntity = new TestEntity();

    @Benchmark
    public void testPutOrderedLong() {

        u.putOrderedLong(testEntity, FIELD_OFFSET, 2);
    }

    @Benchmark
    public void testVolatileStore() {

        testEntity.setId(2);
    }

    public static void main(String[] args) throws RunnerException {
        RunBenchmark.runSimple(VolatileStoreVsPutOrderedSimple.class, TimeUnit.NANOSECONDS);
    }

    public static class TestEntity {

        private volatile long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
