package com.chibik.perf.collections.map;

import java.util.HashMap;
import java.util.Random;
import java.util.concurrent.*;

public class Test1 {

    private static ExecutorService EXECUTOR = Executors.newFixedThreadPool(2);

    public static void main(String[] args) throws Exception {
        for(int i = 0; i < 40; i++) {
            EXECUTOR.submit(new CMapRunner()).get();
        }
        for(int i = 0; i < 40; i++) {
            EXECUTOR.submit(new MapRunner()).get();
        }
    }

    private static class MapRunner implements Runnable {

        public volatile boolean isDone = false;
        private long[] data = new long[1_000_000];
        public HashMap<Long, Long> map = new HashMap<>();

        public MapRunner() {
            Random r = new Random(30L);
            for(int i = 0; i < data.length; i++) {
                data[i] = r.nextInt(50000000);
            }
        }

        @Override
        public void run() {
            int operations = 0;
            long nanoStart = System.nanoTime();

            int index = 0;
            for(int i = 0; i < 1_000_000; i++) {
                if(isDone) {
                    break;
                }

                map.put(data[index++], 1L);
                operations++;
            }
            long nanoElapsed = System.nanoTime() - nanoStart;
            System.out.println("HashMap: operations=" + operations + ", time=" + (nanoElapsed*1.0/1_000_000));
        }
    }

    private static class CMapRunner implements Runnable {

        public volatile boolean isDone = false;
        private long[] data = new long[1_000_000];
        public ConcurrentMap<Long, Long> concurrentMap = new ConcurrentHashMap<>();

        public CMapRunner() {
            Random r = new Random(30L);
            for(int i = 0; i < data.length; i++) {
                data[i] = r.nextInt(50000000);
            }
        }

        @Override
        public void run() {
            int operations = 0;
            long nanoStart = System.nanoTime();

            int index = 0;
            for(int i = 0; i < 1_000_000; i++) {
                if(isDone) {
                    break;
                }

                concurrentMap.put(data[index++], 1L);
                operations++;
            }
            long nanoElapsed = System.nanoTime() - nanoStart;
            System.out.println("ConcurrentHashMap: operations=" + operations + ", time=" + (nanoElapsed*1.0/1_000_000));
        }
    }

}
