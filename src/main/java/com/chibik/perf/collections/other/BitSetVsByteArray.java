package com.chibik.perf.collections.other;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.BitSet;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 10)
@Measurement(iterations = 5)
public class BitSetVsByteArray {

    public static final int NBITS = 1_000_000;

    BitSet bs = new BitSet(NBITS);

    byte[] bytes = new byte[100000000];

    {
        for (int i = 0; i + i < NBITS; i++) {
            bs.set(i);
            bytes[i] = 1;
        }
    }

    @Benchmark
    public int countBitSet() {
        int count = 0;
        for (int i = 0; (i = bs.nextSetBit(i + 1)) >= 0; ) {
            count += i;
        }
        return count;
    }

    @Benchmark
    public int countBytesSet() {
        int count = 0;
        for (int i = 0, bytesLength = bytes.length; i < bytesLength; i++) {
            byte b = bytes[i];
            if (b == 1)
                count += i;
        }
        return count;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(BitSetVsByteArray.class, TimeUnit.NANOSECONDS);
    }
}
