package com.chibik.perf;

import org.openjdk.jmh.results.RunResult;

import java.util.Collection;

public class Main {

    public static void main(String[] args) {
        Collection<RunResult> results = new BenchmarkRunner().runWithStandardOptions();

        BenchmarkReportGenerator reportGenerator = new BenchmarkReportGenerator(
                "test1.pdf",
                results
        );
        reportGenerator.build();
    }
}
