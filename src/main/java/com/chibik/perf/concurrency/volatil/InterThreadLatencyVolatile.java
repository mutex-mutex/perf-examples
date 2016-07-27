package com.chibik.perf.concurrency.volatil;

import com.chibik.perf.concurrency.support.UnsafeTool;
import net.openhft.affinity.Affinity;
import sun.misc.Unsafe;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
    http://mechanical-sympathy.blogspot.ru/2011/08/inter-thread-latency.html

    -XX:+UnlockDiagnosticVMOptions -XX:CompileCommand=print,*PingRunner.run -XX:-TieredCompilation -server
*/
public final class InterThreadLatencyVolatile {

    private static final int REPETITIONS = 100_000_000;

    public static final Unsafe u = UnsafeTool.getUnsafe();

    private static long S1_OFFSET;
    private static long S2_OFFSET;

    static {
        try {

            S1_OFFSET = u.staticFieldOffset(InterThreadLatencyVolatile.class.getDeclaredField("s1"));
            S2_OFFSET = u.staticFieldOffset(InterThreadLatencyVolatile.class.getDeclaredField("s2"));
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

    }

    public static volatile long s1;
    public static volatile long s2;

    public static void main(final String[] args)
            throws Exception
    {
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

    private static long runTest() throws InterruptedException
    {
        final Thread pongThread = new Thread(new PongRunner());
        final Thread pingThread = new Thread(new PingRunner());
        pongThread.start();
        pingThread.start();

        final long start = System.nanoTime();
        pongThread.join();

        return System.nanoTime() - start;
    }

    public static class PingRunner implements Runnable
    {
        public void run()
        {
            for (int i = 0; i < REPETITIONS; i++)
            {
                s1 = i;

                while (i != s2)
                {
                    // busy spin
                }
            }
        }
    }

    public static class PongRunner implements Runnable
    {
        public void run()
        {
            for (int i = 0; i < REPETITIONS; i++)
            {
                while (i != s1)
                {
                    // busy spin
                }

                s2 = i;
            }
        }
    }
}