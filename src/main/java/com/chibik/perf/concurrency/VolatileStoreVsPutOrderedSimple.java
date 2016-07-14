package com.chibik.perf.concurrency;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.misc.Unsafe;
import java.util.concurrent.TimeUnit;

import static com.chibik.perf.concurrency.UnsafeTool.getUnsafe;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
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
    public long testPutOrderedLong() {

        u.putOrderedLong(testEntity, FIELD_OFFSET, 2);

        return testEntity.getId();
    }

    @Benchmark
    public long testVolatileStore() {

        testEntity.setId(2);

        return testEntity.getId();
    }

    public static void main(String[] args) throws RunnerException {
        RunBenchmark.runSimple(VolatileStoreVsPutOrderedSimple.class);
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
