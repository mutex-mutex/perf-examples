package com.chibik.perf.date;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(value = {Mode.AverageTime})
@Measurement(iterations = 5)
public class DateVsLocalDateTimeConstructor {

    private Date d;

    private LocalDateTime ldt;

    private long res;

    @Benchmark
    public void createDate() {
        d = new Date();
    }

    @Benchmark
    public void createLocalDateTime() {
        ldt = LocalDateTime.now();
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        res += d != null ? d.getTime() : 0;
        res += ldt != null ? ldt.getNano() : 0;
    }

    @TearDown(Level.Invocation)
    public void tearDown2() {
        assert res != 0;
    }

    public static void main(String[] args) {
        BenchmarkRunner.runSimple(DateVsLocalDateTimeConstructor.class);
    }
}
