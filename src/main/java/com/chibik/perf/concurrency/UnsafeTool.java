package com.chibik.perf.concurrency;

import sun.misc.Unsafe;

import java.lang.reflect.Field;

public class UnsafeTool {

    protected static Unsafe u;

    public static Unsafe getUnsafe() {
        return u;
    }

    static {
        try {
            Field f = Unsafe.class.getDeclaredField("theUnsafe");
            f.setAccessible(true);

            u = (Unsafe) f.get(null);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
