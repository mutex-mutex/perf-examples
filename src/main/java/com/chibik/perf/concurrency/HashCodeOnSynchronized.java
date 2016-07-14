package com.chibik.perf.concurrency;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

@State(Scope.Benchmark)
@Threads(2)
public class HashCodeOnSynchronized {

    private TestEntity entity;

    @Setup(Level.Iteration)
    public void setUp() {
        entity = new TestEntity();
        entity.s1 = "sagkfhfkf";
        entity.s2 = "54593mfds";
        entity.s3 = "alkjf8923";
        entity.s4 = "fmisdl323";
    }

    @Benchmark
    @Group(value = "sync")
    public int testHashCodeSync() {
        return entity.hashCode();
    }

    @Benchmark
    @Group(value = "sync")
    public void test() throws InterruptedException {
        synchronized (entity) {
            Thread.sleep(500L);
        }
    }

    @Benchmark
    @GroupThreads(value = 1)
    @Group(value = "nonSync")
    public int testHashCodeNonSync() {
        return entity.hashCode();
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(HashCodeOnSynchronized.class);
    }

    public static class TestEntity {
        public String s1;
        public String s2;
        public String s3;
        public String s4;

        /*@Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TestEntity that = (TestEntity) o;

            if (s1 != null ? !s1.equals(that.s1) : that.s1 != null) return false;
            if (s2 != null ? !s2.equals(that.s2) : that.s2 != null) return false;
            if (s3 != null ? !s3.equals(that.s3) : that.s3 != null) return false;
            return s4 != null ? s4.equals(that.s4) : that.s4 == null;

        }

        @Override
        public int hashCode() {
            int result = s1 != null ? s1.hashCode() : 0;
            result = 31 * result + (s2 != null ? s2.hashCode() : 0);
            result = 31 * result + (s3 != null ? s3.hashCode() : 0);
            result = 31 * result + (s4 != null ? s4.hashCode() : 0);
            return result;
        }*/
    }
}
