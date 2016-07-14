package com.chibik.perf.collections;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = 1000000, iterations = 500)
@Measurement(batchSize = 1000000, iterations = 500)
public class ArrayListAdd {

    private List<Integer> arrayList = new ArrayList<>();

    private List<Integer> linkedList = new LinkedList<>();

    @Setup(Level.Iteration)
    public void setUp() {
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();
    }

    @Benchmark
    public boolean addToArrayList() {
        return arrayList.add(2);
    }

    @Benchmark
    public boolean addToLinkedList() {
        return linkedList.add(2);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(ArrayListAdd.class, TimeUnit.MICROSECONDS);
    }
}
