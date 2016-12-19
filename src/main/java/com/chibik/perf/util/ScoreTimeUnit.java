package com.chibik.perf.util;

public enum ScoreTimeUnit {

    SECONDS("s/op",         1_000_000_000),
    MILLISECONDS("ms/op",   1_000_000),
    MICROSECONDS("us/op",   1_000),
    NANOSECONDS("ns/op",    1);

    private final String value;
    private final double ns;

    ScoreTimeUnit(String value, double ns) {
        this.value = value;
        this.ns = ns;
    }

    public static ScoreTimeUnit byTimeUnit(String text) {
        text = text.trim();

        for(ScoreTimeUnit v : values()) {
            if(v.value.equals(text)) {
                return v;
            }
        }

        throw new RuntimeException("Unsupported time unit " + text);
    }

    public String getValue() {
        return value;
    }

    public double getNs() {
        return ns;
    }

    public static Score convertToReadable(Score score) {
        double current = score.getVal();

        for(ScoreTimeUnit v : values()) {
            double multiplier = score.getUnit().ns/v.ns;
            double converted = multiplier * current;
            if(converted >= 1 && converted <= 999.999) {
                return new Score(converted, v);
            }
        }

        double multiplier = score.getUnit().ns/NANOSECONDS.ns;
        double converted = multiplier * current;
        return new Score(converted, NANOSECONDS);
    }
}
