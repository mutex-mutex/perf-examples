package com.chibik.perf.workspace;

import java.util.concurrent.ForkJoinPool;

public class Test1 {

    private static byte shared = 0x1a;

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
                    for(int i = 0; i < 10_000_000; i++) {
                        System.out.print(shared + "/");
                        try {
                            Thread.sleep(100L);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );

        try {
            Thread.sleep(50000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void store () {
        for(int i = 0; i < 100_000_000; i++) {
            shared++;
        }
    }
}
