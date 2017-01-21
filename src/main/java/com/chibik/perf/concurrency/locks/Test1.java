package com.chibik.perf.concurrency.locks;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Test1 {

    protected static class ListLock {
        public static class Node {

            public final AtomicReference<ListLock.Node> next = new AtomicReference<>();
            public final AtomicBoolean locked = new AtomicBoolean(false);
        }

        private AtomicReference<ListLock.Node> tail = new AtomicReference<>();

        public void lock(ListLock.Node node) {
            node.next.set(null);

            ListLock.Node predecessor = tail.getAndSet(node);

            if(predecessor != null) {
                node.locked.set(true);
                predecessor.next.set(node);
                while(node.locked.get());
            }
        }

        public void unlock(ListLock.Node node) {
            if(node.next.get() == null) {
                if(tail.compareAndSet(node, null)) {
                    return;
                }
                while(node.next.get() == null);
            }
            node.next.get().locked.set(false);
        }
    }

    private static int counter = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int threads = 4;
        ExecutorService ex = Executors.newFixedThreadPool(threads);

        ListLock lock = new ListLock();

        List<Future<?>> list = new ArrayList<>();

        for(int i = 0; i < threads; i++) {
            list.add(ex.submit(() -> {
                for(int z = 0; z < 3_000_000/50; z++) {
                    ListLock.Node node = new ListLock.Node();
                    for(int k = 0; k < 50; k++) {
                        lock.lock(node);
                        counter++;
                        lock.unlock(node);
                    }
                }
            }));
        }

        for(Future<?> f : list) {
            f.get();
        }

        System.out.println(counter);
    }
}
