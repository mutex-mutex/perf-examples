package com.chibik.perf.concurrency.queue.juc;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.concurrency.queue.AbstractQueueThroughputOneToOnePerfTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class ABQTest extends AbstractQueueThroughputOneToOnePerfTest {

    private ArrayBlockingQueue<Integer> queue;

    @Override
    public void recreateQueue() {
        queue = new ArrayBlockingQueue<>(1024);
    }

    @Override
    public void addImpl(int x) throws InterruptedException {
        queue.put(x);
    }

    @Override
    public int getImpl() throws InterruptedException {
        return queue.take();
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(ABQTest.class, TimeUnit.NANOSECONDS);
    }
}
