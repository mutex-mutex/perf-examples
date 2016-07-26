package com.chibik.perf.concurrency.volatil;

import net.openhft.affinity.Affinity;

/*
*   http://mechanical-sympathy.blogspot.ru/2011/08/inter-thread-latency.html
* */
public final class InterThreadLatencyOriginal implements Runnable {

    public static final long ITERATIONS = 100_000_000L;

    public static volatile long s1;
    public static volatile long s2;

    public static void main(final String[] args) {

        Thread t = new Thread(new InterThreadLatencyOriginal());
        t.setDaemon(true);
        t.start();

        Affinity.setAffinity(0);

        long start = System.nanoTime();

        long value = s1;
        while (s1 < ITERATIONS)
        {
            while (s2 != value)
            {
                // busy spin
            }
            value = ++s1;
        }

        long duration = System.nanoTime() - start;

        System.out.println("duration = " + duration);
        System.out.println("ns per op = " + duration / (ITERATIONS * 2));
        System.out.println("op/sec = " + (ITERATIONS * 2L * 1000L * 1000L * 1000L) / duration);
        System.out.println("s1 = " + s1 + ", s2 = " + s2);
    }

    public void run()
    {
        Affinity.setAffinity(1);
        long value = s2;
        while (true)
        {
            while (value == s1)
            {
                // busy spin
            }
            value = ++s2;
        }
    }
}