package com.chibik.perf.concurrency.locks;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@State(Scope.Benchmark)
@Threads(1)
@BenchmarkMode(Mode.AverageTime)
@Measurement(timeUnit = TimeUnit.SECONDS)
public class SynchronizedVsReentrantLockOneThread {

    private ReentrantLock lock = new ReentrantLock(false);

    private Object monitor = new Object();

    private long counter;

    @Setup(Level.Iteration)
    public void setUp() {
        counter = 0L;
    }

    @Benchmark
    @Group(value = "sync")
    @GroupThreads(value = 1)
    public long testSynchronized() {
        synchronized (monitor) {
            counter++;
        }
        return counter;
    }

    @Benchmark
    @Group(value = "lock")
    @GroupThreads(value = 1)
    public long testLocked() throws InterruptedException {
        lock.lock();
        counter++;
        lock.unlock();
        return counter;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(SynchronizedVsReentrantLockOneThread.class);
    }
}
