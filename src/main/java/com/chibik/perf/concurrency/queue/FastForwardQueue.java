package com.chibik.perf.concurrency.queue;

import com.chibik.perf.concurrency.support.UnsafeTool;
import sun.misc.Contended;
import sun.misc.Unsafe;

public class FastForwardQueue<T> extends AbstractMyQueue<T> {

    private static Unsafe UNSAFE = UnsafeTool.getUnsafe();

    private static final int BUFFER_ARRAY_BASE;
    private static final long TAIL_OFFSET;
    private static final long HEAD_OFFSET;

    static {
        try  {
            BUFFER_ARRAY_BASE = UNSAFE.arrayBaseOffset(Object[].class);
            TAIL_OFFSET = UNSAFE.objectFieldOffset(FastForwardQueue.class.getDeclaredField("tail"));
            HEAD_OFFSET = UNSAFE.objectFieldOffset(FastForwardQueue.class.getDeclaredField("head"));
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

    public FastForwardQueue(int length) {
        super(length);
        this.buffer = (T[]) new Object[length];
        head = tail = 0;
    }

    @Override
    public boolean enqueue(T data) {
        if(UNSAFE.getObjectVolatile(buffer, sequenceArrayOffset(tail, mask)) != null) {
            return false;
        }

        UNSAFE.putOrderedObject(buffer, sequenceArrayOffset(tail, mask), data);
        UNSAFE.putOrderedInt(this, TAIL_OFFSET, next(tail, mask));

        return true;
    }

    @Override
    public boolean dequeue(DataHolder<T> holder) {
        T d = (T) UNSAFE.getObjectVolatile(buffer, sequenceArrayOffset(head, mask));
        if(d == null) {
            return false;
        }

        UNSAFE.putOrderedObject(buffer, sequenceArrayOffset(head, mask), null);
        UNSAFE.putOrderedInt(this, HEAD_OFFSET, next(head, mask));
        holder.entry = d;

        return true;
    }

    private static int next(int p, int mask) {
        return (p + 1) & mask;
    }

    private static long sequenceArrayOffset(final long sequence, final long mask) {
        return BUFFER_ARRAY_BASE + ((sequence & mask) << 2);
    }
}
