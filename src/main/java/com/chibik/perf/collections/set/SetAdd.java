package com.chibik.perf.collections.set;

import com.chibik.perf.RunBenchmark;
import gnu.trove.set.hash.TIntHashSet;
import org.openjdk.jmh.annotations.*;

import java.util.HashSet;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.SingleShotTime)
@Warmup(batchSize = SetAdd.BATCH_SIZE, iterations = 20, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(batchSize = SetAdd.BATCH_SIZE, iterations = 30, timeUnit = TimeUnit.MILLISECONDS)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class SetAdd {

    @Param({"16", "" + BATCH_SIZE})
    private int preallocatedSize;

    public static final int BATCH_SIZE = 1000000;

    private HashSet<Integer> hashSet;

    private TreeSet<Integer> treeSet;

    private TIntHashSet tIntHashSet;

    private int index;

    @Setup(Level.Iteration)
    public void setUp() {
        index = 0;
        hashSet = new HashSet<>(preallocatedSize);
        treeSet = new TreeSet<>();
        tIntHashSet = new TIntHashSet(preallocatedSize);
    }

    @TearDown(Level.Invocation)
    public void inc() {
        index++;
    }

    @TearDown(Level.Iteration)
    public void validate() {
        if(hashSet.size() + tIntHashSet.size() + treeSet.size() != BATCH_SIZE) {
            throw new RuntimeException(
                    "Invalid iteration!"
            );
        }
    }

    @Benchmark
    public void addToHashSet() {
        hashSet.add(index);
    }

    @Benchmark
    public void addToTIntHashSet() {
        tIntHashSet.add(index);
    }

    @Benchmark
    public void addToTreeSet() {
        treeSet.add(index);
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(SetAdd.class, TimeUnit.MILLISECONDS);
    }
}
