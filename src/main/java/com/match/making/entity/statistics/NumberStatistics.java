package com.match.making.entity.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class NumberStatistics {
    private final BigDecimal sum;
    private final BigDecimal min;
    private final BigDecimal max;

    private NumberStatistics(final BigDecimal sum, final BigDecimal min, final BigDecimal max) {
        this.sum = sum.setScale(2, RoundingMode.HALF_UP);
        this.min = min.setScale(2, RoundingMode.HALF_UP);
        this.max = max.setScale(2, RoundingMode.HALF_UP);
    }

    public static NumberStatistics of(final BigDecimal val) {
        return new NumberStatistics(val, val, val);
    }

    public NumberStatistics merge(final NumberStatistics statistics) {
        final BigDecimal min = this.min.min(statistics.min);
        final BigDecimal max = this.max.max(statistics.max);
        final BigDecimal sum = this.sum.add(statistics.sum);

        return new NumberStatistics(sum, min, max);
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getMin() {
        return min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getAvg(final int size) {
        return sum.divide(BigDecimal.valueOf(size), RoundingMode.HALF_UP);
    }
}
