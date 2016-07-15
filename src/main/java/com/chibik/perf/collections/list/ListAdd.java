package com.chibik.perf.collections.list;

import com.chibik.perf.RunBenchmark;
import gnu.trove.list.TIntList;
import gnu.trove.list.array.TIntArrayList;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(batchSize = 1000000, iterations = 500, timeUnit = TimeUnit.MICROSECONDS)
public class ListAdd {

    private List<Integer> arrayList;

    private List<Integer> linkedList;

    private TIntList tIntList;

    @Setup(Level.Iteration)
    public void setUp() {
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();
        tIntList = new TIntArrayList();
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

    public static void main(String[] args) {
        RunBenchmark.runSimple(ListAdd.class, TimeUnit.MICROSECONDS);
    }
}
