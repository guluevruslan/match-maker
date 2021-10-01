package com.match.making.entity.statistics;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class NumberStatisticsTest {

    private static final int VAL_1 = 4;
    private static final int VAL_2 = 6;
    private static final int AVG = 5;

    @Test
    void checkMerge() {
        final NumberStatistics s1 = NumberStatistics.of(BigDecimal.valueOf(VAL_1));
        final NumberStatistics s2 = NumberStatistics.of(BigDecimal.valueOf(VAL_2));

        final NumberStatistics result = s1.merge(s2);
        assertNotNull(result);

        assertEquals(VAL_1, result.getMin().intValue());
        assertEquals(VAL_2, result.getMax().intValue());
        assertEquals(AVG, result.getAvg(2).intValue());
    }
}