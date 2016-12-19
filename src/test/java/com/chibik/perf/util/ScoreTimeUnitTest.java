package com.chibik.perf.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScoreTimeUnitTest {

    @Test
    public void test() {
        Score score = new Score(0.002, ScoreTimeUnit.MILLISECONDS);

        Score converted = ScoreTimeUnit.convertToReadable(score);

        assertEquals(2.0d, converted.getVal(), 0.00001d);
        assertEquals(ScoreTimeUnit.MICROSECONDS, converted.getUnit());
    }

    @Test
    public void test2() {
        Score score = new Score(0.000002, ScoreTimeUnit.MILLISECONDS);

        Score converted = ScoreTimeUnit.convertToReadable(score);

        assertEquals(2.0d, converted.getVal(), 0.00001d);
        assertEquals(ScoreTimeUnit.NANOSECONDS, converted.getUnit());
    }

}