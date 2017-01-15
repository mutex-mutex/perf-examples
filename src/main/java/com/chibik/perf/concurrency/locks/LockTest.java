package com.chibik.perf.concurrency.locks;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Comment;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.LockSupport;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 10)
@Measurement(iterations = 10, timeUnit = TimeUnit.SECONDS)
@Comment(
        "Algorithms from 'Algorithms for scalable synchronization on " +
        "shared-memory multiprocessors' paper, John M. Mellor-Crummey, Michael L. Scott")
public class LockTest {

    private long counter;

    private PlainTASLock plainTasLock = new PlainTASLock();

    private PlainTTASLock plainTTASLock = new PlainTTASLock();

    private PlainTTASLockWithBackoff tasWithBackoff1Ns = new PlainTTASLockWithBackoff(1);
    private PlainTTASLockWithBackoff tasWithBackoff5Ns = new PlainTTASLockWithBackoff(5);
    private PlainTTASLockWithBackoff tasWithBackoff20Ns = new PlainTTASLockWithBackoff(20);

    @Setup(Level.Iteration)
    public void setUp() {
        counter = 0L;
    }

    /*
    @Benchmark
    @GroupThreads(4)
    @Group("plainTasLock")
    @Comment("Plain Test-And-Set lock. Just spin using CAS and nothing else")
    public void plainTasLock() {
        plainTasLock.lock();
        try {

            counter++;
        } finally {
            plainTasLock.unlock();
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("plainTtasLock")
    @Comment("Plain Test-Test-And-Set lock. Same as TAS but spins only if read " +
            "of flag observed 'false' value")
    public void plainTtasLock() {
        plainTTASLock.lock();
        try {

            counter++;
        } finally {
            plainTTASLock.unlock();
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("plainTasWith1nsBackoff")
    @Comment("Plain Test-And-Set lock with 1ns backoff")
    public void plainTasLockWith1NsBackoff() {
        tasWithBackoff1Ns.lock();
        try {

            counter++;
        } finally {
            tasWithBackoff1Ns.unlock();
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("plainTasWith5nsBackoff")
    @Comment("Plain Test-And-Set lock with 5ns backoff")
    public void plainTasLockWith5NsBackoff() {
        tasWithBackoff5Ns.lock();
        try {

            counter++;
        } finally {
            tasWithBackoff5Ns.unlock();
        }
    }
    */

    @Benchmark
    @GroupThreads(4)
    @Group("plainTasWith20nsBackoff")
    @Comment("Plain Test-And-Set lock with 20ns backoff")
    public void plainTasLockWith20NsBackoff() {
        tasWithBackoff20Ns.lock();
        try {

            counter++;
        } finally {
            tasWithBackoff20Ns.unlock();
        }
    }

    public static class PlainTASLock {

        private AtomicBoolean locked = new AtomicBoolean(false);

        public void lock() {
            while(!locked.compareAndSet(false, true));
        }

        public void unlock() {
            locked.set(false);
        }
    }

    public static class PlainTTASLock {

        private AtomicBoolean locked = new AtomicBoolean(false);

        public void lock() {
            while(true) {
                if(!locked.get()) {
                    if(locked.compareAndSet(false, true)) {
                        return;
                    }
                }
            }
        }

        public void unlock() {
            locked.set(false);
        }
    }

    public static class PlainTTASLockWithBackoff {

        private final long backoffNanos;
        private AtomicBoolean locked = new AtomicBoolean(false);

        public PlainTTASLockWithBackoff(long backoffNanos) {
            this.backoffNanos = backoffNanos;
        }

        public void lock() {
            while(true) {
                if(locked.compareAndSet(false, true)) {
                    return;
                } else {
                    LockSupport.parkNanos(backoffNanos);
                }
            }
        }

        public void unlock() {
            locked.set(false);
        }
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(LockTest.class);
    }
}
