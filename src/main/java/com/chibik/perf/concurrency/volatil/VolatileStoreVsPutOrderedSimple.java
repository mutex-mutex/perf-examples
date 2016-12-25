package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Included;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.RunnerException;
import sun.misc.Unsafe;
import java.util.concurrent.TimeUnit;

import static com.chibik.perf.util.UnsafeTool.getUnsafe;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 40, batchSize = VolatileStoreVsPutOrderedSimple.BATCH_SIZE)
@Measurement(iterations = 40, batchSize = VolatileStoreVsPutOrderedSimple.BATCH_SIZE)
@Included
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

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testPutOrderedLongToVolatile() {

        u.putOrderedLong(testEntity, VOLATILE_OFFSET, 2);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testVolatileStore() {

        testEntity.setId(2);
    }

    @Benchmark
    @CompilerControl(CompilerControl.Mode.DONT_INLINE)
    public void testNormalStore() {

        testEntity.setIdNormal(2);
    }

    public static void main(String[] args) throws RunnerException {
        BenchmarkRunner.runSimple(VolatileStoreVsPutOrderedSimple.class, TimeUnit.MICROSECONDS );
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
