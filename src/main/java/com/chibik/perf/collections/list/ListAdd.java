package com.chibik.perf.collections.list;

import com.chibik.perf.BenchmarkRunner;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.openjdk.jmh.annotations.*;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = ListAdd.BATCH_SIZE, iterations = 40, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(batchSize = ListAdd.BATCH_SIZE, iterations = 40, timeUnit = TimeUnit.MILLISECONDS)
public class ListAdd {

    public static final int BATCH_SIZE = 1000000;

    @Param({"16", "" + BATCH_SIZE})
    private int initialCapacity;

    private List<Integer> arrayList;

    private List<Integer> linkedList;

    private TIntList tIntList;

    private ByteArrayOutputStream baos;

    @Setup(Level.Iteration)
    public void setUp() {
        arrayList = new ArrayList<>(initialCapacity);
        linkedList = new LinkedList<>();
        tIntList = new TIntArrayList(initialCapacity);
        baos = new ByteArrayOutputStream(initialCapacity);
    }

    @Benchmark
    public boolean addToArrayList() {
        return arrayList.add(2);
    }

    @Benchmark
    public boolean addToLinkedList() {
        return linkedList.add(2);
    }

    @Benchmark
    public boolean addToTIntList() {
        return tIntList.add(2);
    }

    @Benchmark
    public void addToByteArrayOutputStream() {
        baos.write((byte) 2);
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(ListAdd.class, TimeUnit.MILLISECONDS);
    }
}