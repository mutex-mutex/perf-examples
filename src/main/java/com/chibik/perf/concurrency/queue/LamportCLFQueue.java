package com.chibik.perf.concurrency.queue;

import com.chibik.perf.concurrency.support.UnsafeTool;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class LamportCLFQueue<T> {

    private static Unsafe UNSAFE = UnsafeTool.getUnsafe();

    private static final int BUFFER_ARRAY_BASE;

    static {
        try  {
            BUFFER_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Contended
    private volatile int head;
    @Contended
    private volatile int tail;

    private final int capacity;

    private final int mask;

    @Contended
    private final T[] buffer;

    public LamportCLFQueue(int length) {
        this.buffer = (T[]) new Object[length];
        this.capacity = length;
        this.mask = (length - 1);
        head = tail = 0;
    }

    public boolean enqueue(T data) {
        if(next(tail) == head) {
            return false;
        }

        buffer[tail] = data;
        tail = next(tail);
        return true;
    }

    public boolean dequeue(DataHolder<T> holder) {
        if(head == tail) {
            return false;
        }

        holder.entry = buffer[head];
        head = next(head);
        return true;
    }

    private int next(int p) {
        return (p + 1) & mask;
    }

    private long sequenceArrayOffset(final long sequence) {
        return BUFFER_ARRAY_BASE + ((sequence & mask) << 3);
    }

    public static void main(String[] args) {
        LamportCLFQueue<Integer> queue = new LamportCLFQueue<>(4);
        System.out.println(queue.enqueue(1));
        System.out.println(queue.enqueue(1));
        System.out.println(queue.enqueue(1));
        System.out.println(queue.enqueue(1));
        System.out.println();
    }
}
