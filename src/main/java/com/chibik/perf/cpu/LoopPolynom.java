package com.chibik.perf.cpu;

import com.chibik.perf.BenchmarkRunner;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
@Warmup(iterations = 5)
@Measurement(iterations = 5)
public class LoopPolynom {

    private double[] table = new double[1000];

    @Benchmark
    public double[] a() {
        double A = 1.1, B = 2.2, C = 3.3;

        for(int x = 0; x < 1000; x++) {
            table[x] = A*x*x + B*x + C;
        }

        return table;
    }

    @Benchmark
    public double[] b() {
        double A = 1.1, B = 2.2, C = 3.3;

        double A2 = A + A;
        double Y = C;
        double Z = A + B;
        for(int x = 0; x < 1000; x++) {
            table[x] = Y;
            Y += Z;
            Z += A2;
        }

        return table;
    }

    public static void main(String[] args) {
        
        BenchmarkRunner.runSimple(LoopPolynom.class);
    }
}
