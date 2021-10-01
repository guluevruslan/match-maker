package com.match.making.entity.statistics;

import com.match.making.entity.user.User;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class UserGroupStatistics {
    private static final int NANOS_IN_SECOND = 1_000_000_000;
    private final NumberStatistics skill;
    private final NumberStatistics latency;
    private final NumberStatistics time;

    private UserGroupStatistics(final NumberStatistics skill,
                                final NumberStatistics latency,
                                final NumberStatistics time) {
        this.skill = skill;
        this.latency = latency;
        this.time = time;
    }

    public static UserGroupStatistics of(final User user, final long currentTime) {
        final NumberStatistics skill = NumberStatistics.of(BigDecimal.valueOf(user.getSkill()));
        final NumberStatistics latency = NumberStatistics.of(BigDecimal.valueOf(user.getLatency()));
        final NumberStatistics time = NumberStatistics.of(BigDecimal.valueOf(currentTime)
                .subtract(BigDecimal.valueOf(user.getRegistrationTime())));
        return new UserGroupStatistics(skill, latency, time);
    }

    public UserGroupStatistics merge(final UserGroupStatistics userGroupStatistics) {
        final NumberStatistics skill = this.skill.merge(userGroupStatistics.skill);
        final NumberStatistics latency = this.latency.merge(userGroupStatistics.latency);
        final NumberStatistics time = this.time.merge(userGroupStatistics.time);

        return new UserGroupStatistics(skill, latency, time);
    }

    public BigDecimal getMinSkill() {
        return skill.getMin();
    }

    public BigDecimal getMaxSkill() {
        return skill.getMax();
    }

    public BigDecimal getAvgSkill(final int size) {
        return skill.getAvg(size);
    }

    public BigDecimal getMinLatency() {
        return latency.getMin();
    }

    public BigDecimal getMaxLatency() {
        return latency.getMax();
    }

    public BigDecimal getAvgLatency(final int size) {
        return latency.getAvg(size);
    }

    public BigDecimal getMinTime() {
        return toSeconds(time.getMin());
    }

    public BigDecimal getMaxTime() {
        return toSeconds(time.getMax());
    }

    public BigDecimal getAvgTime(final int size) {
        return toSeconds(time.getAvg(size));
    }

    private static BigDecimal toSeconds(final BigDecimal time) {
        return time.divide(BigDecimal.valueOf(NANOS_IN_SECOND), RoundingMode.HALF_UP)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
