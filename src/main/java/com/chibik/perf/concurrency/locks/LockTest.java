package com.chibik.perf.concurrency.locks;

import com.chibik.perf.BenchmarkRunner;
import com.chibik.perf.util.Comment;
import com.chibik.perf.util.MultipleOps;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

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
    private TicketLock ticketLock = new TicketLock();
    private ArrayBasedLock arrayBasedLock = new ArrayBasedLock();
    private ReentrantLock reentrantLock = new ReentrantLock();
    private Object object = new Object();
    private ListLockV1 listLockV1 = new ListLockV1();

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

    @Benchmark
    @GroupThreads(4)
    @Group("ticketLock")
    @Comment("Ticket lock")
    public void ticketLock() {
        ticketLock.lock();
        try {

            counter++;
        } finally {
            ticketLock.unlock();
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("arrayBasedLock")
    @Comment("Array based lock")
    @MultipleOps(50)
    public void arrayBasedLock() {
        int[] slot = new int[1];
        for(int i = 0; i < 50; i++) {
            arrayBasedLock.lock(slot);
            try {

                counter++;
            } finally {
                arrayBasedLock.unlock(slot);
            }
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("reentrantLock")
    @Comment("Reentrant lock")
    public void reentrantLock() {
        reentrantLock.lock();
        try {

            counter++;
        } finally {
            reentrantLock.unlock();
        }
    }

    @Benchmark
    @GroupThreads(4)
    @Group("synchornized")
    @Comment("Synchornized")
    public void synchronizedd() {

        synchronized (object) {
            counter++;
        }
    }
    */

    @Benchmark
    @GroupThreads(4)
    @Group("mcsLock")
    @Comment("MCS lock")
    @MultipleOps(100)
    public void mcsLock() {
        ListLockV1.Node node = new ListLockV1.Node();

        for(int i = 0; i < 100; i++) {
            listLockV1.lock(node);
            try {

                counter++;
            } finally {
                listLockV1.unlock(node);
            }
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

    public static class TicketLock {

        private AtomicLong nextTicket = new AtomicLong(0);
        private AtomicLong nowServing = new AtomicLong(0);

        public void lock() {
            long myTicket = nextTicket.getAndIncrement();

            while(true) {
//                LockSupport.parkNanos(myTicket - nowServing.get());

                if(nowServing.get() == myTicket) {
                    return;
                }
            }
        }

        public void unlock() {
            nowServing.lazySet(nowServing.get() + 1);
        }
    }

    public static class ArrayBasedLock {

        private AtomicIntegerArray array = new AtomicIntegerArray(4 * 32);
        private AtomicInteger nextSlot = new AtomicInteger(0);

        {
            array.set(0, 1);
        }

        public void lock(int[] slot) {
            int myPlace = nextSlot.getAndIncrement();

            if(myPlace % 4 == 0) {
                nextSlot.addAndGet(-4);
            }

            myPlace = myPlace & 3;
            slot[0] = myPlace;
            int realSlot = myPlace << 5;
            while(array.get(realSlot) == 0);
            array.set(realSlot, 0);
        }

        public void unlock(int[] slot) {
            int myPlace = slot[0];
            int next = (myPlace + 1) & 3;
            array.set(next << 5, 1);
        }
    }

    protected static class ListLockV1 {

        public static class Node {

            public final AtomicReference<ListLockV1.Node> next = new AtomicReference<>();
            public final AtomicBoolean locked = new AtomicBoolean(false);
        }

        private AtomicReference<ListLockV1.Node> tail = new AtomicReference<>();

        public void lock(ListLockV1.Node node) {
            node.next.set(null);

            ListLockV1.Node predecessor = tail.getAndSet(node);

            if(predecessor != null) {
                node.locked.set(true);
                predecessor.next.set(node);
                while(node.locked.get());
            }
        }

        public void unlock(ListLockV1.Node node) {
            if(node.next.get() == null) {
                if(tail.compareAndSet(node, null)) {
                    return;
                }
                while(node.next.get() == null);
            }
            node.next.get().locked.set(false);
        }
    }

    protected static class ListLockV2 {

        public static class NodeV2 {

            public volatile NodeV2 next = null;
            public volatile boolean locked = false;
        }

        private volatile NodeV2 tail;

        public void lock(ListLockV1.Node node) {
            node.next.set(null);

            ListLockV1.Node predecessor = tail.getAndSet(node);

            if(predecessor != null) {
                node.locked.set(true);
                predecessor.next.set(node);
                while(node.locked.get());
            }
        }

        public void unlock(ListLockV1.Node node) {
            if(node.next.get() == null) {
                if(tail.compareAndSet(node, null)) {
                    return;
                }
                while(node.next.get() == null);
            }
            node.next.get().locked.set(false);
        }
    }

    public static void main(String[] args) {

        BenchmarkRunner.runSimple(LockTest.class);
    }
}
