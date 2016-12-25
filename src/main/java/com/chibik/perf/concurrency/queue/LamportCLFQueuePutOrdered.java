package com.chibik.perf.concurrency.queue;

import com.chibik.perf.util.UnsafeTool;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class LamportCLFQueuePutOrdered<T> extends AbstractMyQueue<T> {

    private static Unsafe UNSAFE = UnsafeTool.getUnsafe();

    private static final int BUFFER_ARRAY_BASE;
    private static final long TAIL_OFFSET;
    private static final long HEAD_OFFSET;

    static {
        try  {
            BUFFER_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);
            TAIL_OFFSET = UNSAFE.objectFieldOffset(LamportCLFQueuePutOrdered.class.getDeclaredField("tail"));
            HEAD_OFFSET = UNSAFE.objectFieldOffset(LamportCLFQueuePutOrdered.class.getDeclaredField("head"));
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Contended
    private volatile int head;
    @Contended
    private volatile int tail;

    @Contended
    private final T[] buffer;

    public LamportCLFQueuePutOrdered(int length) {
        super(length);
        this.buffer = (T[]) new Object[length];
        head = tail = 0;
    }

    public boolean enqueue(T data) {
        if(head == next(tail, mask)) {
            return false;
        }

        UNSAFE.putOrderedObject(buffer, sequenceArrayOffset(tail, mask), data);
        UNSAFE.putOrderedInt(this, TAIL_OFFSET, next(tail, mask));

        return true;
    }

    public boolean dequeue(DataHolder<T> holder) {
        if(head == tail) {
            return false;
        }

        holder.entry = (T) UNSAFE.getObjectVolatile(buffer, sequenceArrayOffset(head, mask));
        UNSAFE.putOrderedInt(this, HEAD_OFFSET, next(head, mask));

        return true;
    }

    private static int next(int p, int mask) {
        return (p + 1) & mask;
    }

    private static long sequenceArrayOffset(final long sequence, final long mask) {
        return BUFFER_ARRAY_BASE + ((sequence & mask) << 2);
    }
}
