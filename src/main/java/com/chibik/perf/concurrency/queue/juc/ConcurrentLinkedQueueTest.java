package com.chibik.perf.concurrency.queue.juc;

import com.chibik.perf.RunBenchmark;
import com.chibik.perf.concurrency.queue.AbstractQueueThroughputOneToOnePerfTest;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class ConcurrentLinkedQueueTest extends AbstractQueueThroughputOneToOnePerfTest {

    private ConcurrentLinkedQueue<Integer> queue;

    @Override
    public void recreateQueue() {
        queue = new ConcurrentLinkedQueue<>();
    }

    @Override
    public void addImpl(int x) throws InterruptedException {
        queue.add(x);
    }

    @Override
    public int getImpl() throws InterruptedException {
        Integer val;
        while((val = queue.poll()) == null) {}
        return val;
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(ConcurrentLinkedQueueTest.class, TimeUnit.NANOSECONDS);
    }
}
