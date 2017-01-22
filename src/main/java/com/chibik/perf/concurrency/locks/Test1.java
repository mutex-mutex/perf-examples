package com.chibik.perf.concurrency.locks;

import com.chibik.perf.util.UnsafeTool;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Test1 {

    public static class ListLockV2 {

        public static long NODE_NEXT_OFFSET;
        public static long NODE_LOCKED_OFFSET;
        public static long LOCK_TAIL_OFFSET;
        public static Unsafe unsafe = UnsafeTool.getUnsafe();

        public static class NodeV2 {

            public volatile NodeV2 next = null;
            public volatile boolean locked = false;
        }

        static {
            try {

                Field nextField = NodeV2.class.getDeclaredField("next");
                NODE_NEXT_OFFSET = unsafe.objectFieldOffset(nextField);
                Field lockedField = NodeV2.class.getDeclaredField("locked");
                NODE_LOCKED_OFFSET = unsafe.objectFieldOffset(lockedField);
                Field tailField = ListLockV2.class.getDeclaredField("tail");
                tailField.setAccessible(true);
                LOCK_TAIL_OFFSET = unsafe.objectFieldOffset(tailField);
            } catch (Exception e) {

                throw new RuntimeException(e);
            }
        }

        private volatile NodeV2 tail;

        public void lock(ListLockV2.NodeV2 node) {
            unsafe.putOrderedObject(node, NODE_NEXT_OFFSET, null);

            ListLockV2.NodeV2 predecessor = (ListLockV2.NodeV2)
                    unsafe.getAndSetObject(this, LOCK_TAIL_OFFSET, node);

            if(predecessor != null) {
                unsafe.putOrderedObject(node, NODE_LOCKED_OFFSET, true);
                unsafe.putObjectVolatile(predecessor, NODE_NEXT_OFFSET, node);
                while(unsafe.getBooleanVolatile(node, NODE_LOCKED_OFFSET));
            }
        }

        public void unlock(ListLockV2.NodeV2 node) {
            if(unsafe.getObjectVolatile(node, NODE_NEXT_OFFSET) == null) {
                if(unsafe.compareAndSwapObject(this, LOCK_TAIL_OFFSET, node, null)) {
                    return;
                }
                while(unsafe.getObjectVolatile(node, NODE_NEXT_OFFSET) == null);
            }
            NodeV2 next = (NodeV2) unsafe.getObjectVolatile(node, NODE_NEXT_OFFSET);
            unsafe.putBooleanVolatile(next, NODE_LOCKED_OFFSET, false);
        }
    }

    private static int counter = 0;

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        int threads = 4;
        ExecutorService ex = Executors.newFixedThreadPool(threads + 1);

        ListLockV2 lock = new ListLockV2();

        List<Future<?>> list = new ArrayList<>();

        for(int i = 0; i < threads; i++) {
            list.add(ex.submit(() -> {
                for(int z = 0; z < 3_000_000/50; z++) {
                    ListLockV2.NodeV2 node = new ListLockV2.NodeV2();
                    for(int k = 0; k < 50; k++) {
                        lock.lock(node);
                        counter++;
                        lock.unlock(node);
                    }
                }
            }));
        }

        ex.submit(
                () -> {
                    while(true) {
                        try {
                            Thread.sleep(500L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        int counterLocal = counter;
                        System.out.println(counterLocal);
                        if(counterLocal == 12_000_000) {
                            return;
                        }
                    }
                }
        );

        ex.shutdown();
        ex.awaitTermination(1, TimeUnit.HOURS);

        System.out.println("final value: " + counter);
    }
}
