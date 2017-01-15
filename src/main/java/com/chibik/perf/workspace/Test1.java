package com.chibik.perf.workspace;

import java.util.concurrent.ForkJoinPool;

public class Test1 {

    private static byte shared = 0x1c;

    static {
        System.out.println("initial=" + shared + "/");
    }

    public static void main(String[] args) {
        ForkJoinPool.commonPool().submit(
                () -> {
                    store();
                }
        );

        ForkJoinPool.commonPool().submit(
                () -> {
                    System.out.println(load());
                }
        );

        try {
            Thread.sleep(50000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void store() {
        for(int i = 0; i < 100_000_000; i++) {
            shared++;
        }
    }

    private static int load() {
        int z = 0;
        for(int i = 0; i < (2 << 4) * 100_000; i++) {
            byte v = shared;
            z ^= v;
        }
        return z;
    }
}
