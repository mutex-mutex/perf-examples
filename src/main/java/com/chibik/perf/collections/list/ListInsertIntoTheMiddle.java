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
@Warmup(batchSize = 1, iterations = 50000, timeUnit = TimeUnit.MICROSECONDS)
@Measurement(batchSize = 1, iterations = 50000, timeUnit = TimeUnit.MICROSECONDS)
public class ListInsertIntoTheMiddle {

    private List<Integer> arrayList;

    private List<Integer> linkedList;

    private TIntList tIntList;

    @Param({"10", "100", "1000", "1000000"})
    private int length;

    @Setup(Level.Trial)
    public void setUp() {
        arrayList = new ArrayList<>();
        linkedList = new LinkedList<>();
        tIntList = new TIntArrayList();

        for(int i = 0; i < length; i++) {
            arrayList.add(2);
            linkedList.add(2);
            tIntList.add(2);
        }
    }

    @Benchmark
    public void addToArrayListAtTheMiddle() {
        arrayList.add(length/2, 2);
    }

    @Benchmark
    public void addToLinkedListAtTheMiddle() {
        linkedList.add(length/2, 2);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(ListInsertIntoTheMiddle.class, TimeUnit.MICROSECONDS);
    }
}
