package com.chibik.perf.concurrency.volatil;

import sun.misc.Contended;

public class DataSharingVolatile {

    private static final int REPETITIONS = 50_000_000;

    @Contended
    private volatile Object object;
    @Contended
    public volatile long s1;
    @Contended
    public volatile long s2;

    public static void main(String[] args) throws InterruptedException {
        DataSharingVolatile t = new DataSharingVolatile();
        t.runAll();
    }

    public void runAll() throws InterruptedException {
        for (int i = 0; i < 2; i++)
        {
            final long duration = runTest();

            System.out.printf("%d - %d per sec - %dns avg latency - ping=%d pong=%d\n",
                    i,
                    (REPETITIONS * 2L * 1000L * 1000L * 1000L) / duration,
                    duration / (REPETITIONS * 2),
                    s1,
                    s2);
        }
    }

    private long runTest() throws InterruptedException
    {
        final Thread pongThread = new Thread(new DataSharingVolatile.Producer());
        final Thread pingThread = new Thread(new DataSharingVolatile.Consumer());
        pongThread.start();
        pingThread.start();

        final long start = System.nanoTime();
        pongThread.join();

        return System.nanoTime() - start;
    }

    public class Producer implements Runnable
    {
        public void run()
        {
            for (int i = 1; i < REPETITIONS; i++)
            {

                object = new Object();
                s1 = i;

                while (i != s2)
                {
                    // busy spin
                }
            }
        }
    }

    public class Consumer implements Runnable
    {
        public void run()
        {
            Object lastSeen = new Object();

            for (int i = 1; i < REPETITIONS; i++)
            {
                while (lastSeen == object)
                {
                    // busy spin
                }

                lastSeen = object;//consume object

                s2 = i;
            }
        }
    }

}
