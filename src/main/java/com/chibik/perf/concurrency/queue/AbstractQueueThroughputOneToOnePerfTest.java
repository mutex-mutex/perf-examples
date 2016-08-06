package com.chibik.perf.concurrency.queue;

import org.openjdk.jmh.annotations.*;
import sun.misc.Contended;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public abstract class AbstractQueueThroughputOneToOnePerfTest {

    public static final int BATCH_SIZE = 10_000_000;

    public abstract void recreateQueue();

    public abstract void addImpl(int x) throws InterruptedException;

    public abstract int getImpl() throws InterruptedException;

    @Contended
    private int item;

    @Contended
    private int result;

    @Contended
    private volatile int membar = 0;

    @Setup(Level.Iteration)
    public void setUp() {
        item = 0;
        result = 0;
        recreateQueue();
        membar = 1;

        System.gc();
        System.gc();
        System.gc();
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
        int expected = 0;
        for(int i = 1; i < BATCH_SIZE + 1; i++) {
            expected += 1;
        }

        if(result != expected) {
            throw new RuntimeException("Was " + result + ", expected " + expected);
        }
    }

    @GroupThreads(value = 1)
    @Group("queuetest")
    @Benchmark
    @Warmup(iterations = 15, batchSize = AbstractQueueThroughputOneToOnePerfTest.BATCH_SIZE)
    @Measurement(iterations = 10, batchSize = AbstractQueueThroughputOneToOnePerfTest.BATCH_SIZE)
    @BenchmarkMode(Mode.SingleShotTime)
    public void put() throws InterruptedException {

        addImpl(1);
    }

    @GroupThreads(value = 1)
    @Group("queuetest")
    @Benchmark
    @Warmup(iterations = 15, batchSize = AbstractQueueThroughputOneToOnePerfTest.BATCH_SIZE)
    @Measurement(iterations = 10, batchSize = AbstractQueueThroughputOneToOnePerfTest.BATCH_SIZE)
    @BenchmarkMode(Mode.SingleShotTime)
    public int get() throws InterruptedException {

        result += getImpl();
        return result;
    }
}
