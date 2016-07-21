package com.chibik.perf.date;

import com.chibik.perf.RunBenchmark;
import org.openjdk.jmh.annotations.*;

import java.time.LocalDateTime;
import java.util.Date;

@State(Scope.Benchmark)
@BenchmarkMode(value = {Mode.AverageTime})
@Measurement(iterations = 5)
public class GetMonthDateVsLocalDateTime {

    private Date d = new Date();

    private LocalDateTime ldt = LocalDateTime.now();

    @Benchmark
    public int getMonthFromDate() {
        return d.getMonth();
    }

    @Benchmark
    public int getMonthFromLocalDateTime() {
        return ldt.getMonth().getValue();
    }

    @Benchmark
    public int getDayFromDate() {
        return d.getDay();
    }

    @Benchmark
    public int getDayFromLocalDateTime() {
        return ldt.getDayOfMonth();
    }

    public static void main(String[] args) {
        RunBenchmark.runSimple(GetMonthDateVsLocalDateTime.class);
    }
}
