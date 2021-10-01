package com.match.making.entity.statistics;

import com.match.making.entity.user.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class UserGroupStatisticsTest {

    private static final int LATENCY_1 = 10;
    private static final int LATENCY_2 = 8;
    private static final int AVG_LATENCY = 9;
    private static final int SKILL_1 = 20;
    private static final int SKILL_2 = 40;
    private static final int AVG_SKILL = 30;
    private static final long FIVE_SECOND_IN_NANOS = 5_000_000_000L;

    @Test
    void checkValidStatisticsForUserGroup() {
        final User user1 = User.builder()
                .name("NAME")
                .latency(LATENCY_1)
                .skill(SKILL_1)
                .build();
        final User user2 = User.builder()
                .name("NAME")
                .latency(LATENCY_2)
                .skill(SKILL_2)
                .build();
        final long time = System.nanoTime() + FIVE_SECOND_IN_NANOS;
        final UserGroupStatistics result = UserGroupStatistics.of(user1, time)
                .merge(UserGroupStatistics.of(user2, time));

        assertNotNull(result);
        assertEquals(LATENCY_2, result.getMinLatency().intValue());
        assertEquals(LATENCY_1, result.getMaxLatency().intValue());
        assertEquals(AVG_LATENCY, result.getAvgLatency(2).intValue());

        assertEquals(SKILL_1, result.getMinSkill().intValue());
        assertEquals(SKILL_2, result.getMaxSkill().intValue());
        assertEquals(AVG_SKILL, result.getAvgSkill(2).intValue());

        assertTrue(result.getMinTime().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getMaxTime().compareTo(BigDecimal.ZERO) > 0);
        assertTrue(result.getAvgTime(2).compareTo(BigDecimal.ZERO) > 0);
    }
}