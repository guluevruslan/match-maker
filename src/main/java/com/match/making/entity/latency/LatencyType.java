package com.match.making.entity.latency;

import java.util.stream.Stream;

public enum LatencyType {
    LOW(0, 50),
    MEDIUM(50, 100),
    HIGH(100, Double.MAX_VALUE);

    private final double lowerBound;
    private final double upperBound;

    LatencyType(final double lowerBound, final double upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public static LatencyType defineLatencyType(final double latency) {
        return Stream.of(values())
                .filter(latencyType -> latencyType.isInBound(latency))
                .findAny()
                .orElseThrow(() -> new RuntimeException("Unable to define latency type for: " + latency));
    }

    private boolean isInBound(final double latency) {
        return lowerBound <= latency
                && upperBound > latency;
    }
}
