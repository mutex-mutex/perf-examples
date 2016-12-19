package com.chibik.perf.util;

public class Score {

    private final double val;
    private final ScoreTimeUnit unit;

    public Score(double val, ScoreTimeUnit unit) {
        this.val = val;
        this.unit = unit;
    }

    public double getVal() {
        return val;
    }

    public ScoreTimeUnit getUnit() {
        return unit;
    }
}
