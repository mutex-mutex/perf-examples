package com.chibik.perf.concurrency.queue;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.Param;

import java.util.concurrent.TimeUnit;

public class LamportCLFQueueTest extends AbstractQueueThroughputOneToOnePerfTest {

    private LamportCLFQueue<Integer> queue;

    @Param({"1024", "16384", "131072"})
    private int len;

    @Override
    public void recreateQueue() {
        queue = new LamportCLFQueue<>(len);
    }

    @Override
    public void addImpl(int x) throws InterruptedException {
        while(!queue.enqueue(x));
    }

    @Override
    public int getImpl() throws InterruptedException {
        DataHolder<Integer> data = new DataHolder<>();
        while(!queue.dequeue(data));
        return data.entry;
    }

    public static void main(String[] args) {

        RunBenchmark.runSimple(LamportCLFQueueTest.class, TimeUnit.NANOSECONDS);
    }
}
