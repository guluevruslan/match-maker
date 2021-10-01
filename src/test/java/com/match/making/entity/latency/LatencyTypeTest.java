package com.match.making.entity.latency;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LatencyTypeTest {

    @ParameterizedTest
    @MethodSource("latencyWithExpectedType")
    void checkForValidUserType(final double latency, final LatencyType expectedType) {
        assertEquals(expectedType, LatencyType.defineLatencyType(latency));
    }

    private static Stream<Arguments> latencyWithExpectedType() {
        return Stream.of(
                Arguments.of(0D, LatencyType.LOW),
                Arguments.of(1D, LatencyType.LOW),
                Arguments.of(10D, LatencyType.LOW),
                Arguments.of(49D, LatencyType.LOW),
                Arguments.of(50D, LatencyType.MEDIUM),
                Arguments.of(51D, LatencyType.MEDIUM),
                Arguments.of(60D, LatencyType.MEDIUM),
                Arguments.of(99D, LatencyType.MEDIUM),
                Arguments.of(100D, LatencyType.HIGH),
                Arguments.of(101D, LatencyType.HIGH),
                Arguments.of(300D, LatencyType.HIGH)
        );
    }
}