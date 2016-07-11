package com.chibik.perf.primitives;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import static com.chibik.perf.primitives.UnsafeTool.getUnsafe;

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

    private ThreadLocalRandom random = ThreadLocalRandom.current();

    @Benchmark
    public long testPutOrderedLong() {
        long id = random.nextLong();

        u.putOrderedLong(testEntity, FIELD_OFFSET, id);

        return testEntity.getId();
    }

    @Benchmark
    public long testVolatileStore() {
        long id = random.nextLong();

        testEntity.setId(id);

        return testEntity.getId();
    }

    public void run() throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(".*" + this.getClass().getSimpleName() + ".*")
                .forks(1)
                .warmupTime(TimeValue.seconds(3))
                .warmupIterations(3)
                .measurementTime(TimeValue.seconds(1))
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.NANOSECONDS)
                .jvmArgsAppend("-Xmx2G")
                .build();

        new Runner(opt).run();
    }

    public static void main(String[] args) throws RunnerException {
        new VolatileStoreVsPutOrderedSimple().run();
    }

    public static class TestEntity {

        private long id;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }
    }
}
