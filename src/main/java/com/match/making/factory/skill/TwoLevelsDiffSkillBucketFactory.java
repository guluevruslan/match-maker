package com.match.making.factory.skill;

import com.match.making.entity.skill.SkillBucket;
import com.match.making.factory.Factory;

public class TwoLevelsDiffSkillBucketFactory implements Factory.SkillBucket {
    private static final int MAX_LEVEL_RANGE = 2;
    private static final int LOWEST_LEVEL = 1;

    @Override
    public SkillBucket createFrom(final double skillVal) {
        final long skillValAsLong = Math.round(skillVal);

        if (isLowestLevel(skillValAsLong)) {
            return lowestBucket();
        }

        return bucketFor(skillValAsLong);
    }

    @Override
    public SkillBucket nextFor(final SkillBucket bucket) {
        return SkillBucket.builder()
                .lowerBound(bucket.getLowerBound() + MAX_LEVEL_RANGE)
                .upperBound(bucket.getUpperBound() + MAX_LEVEL_RANGE)
                .build();
    }

    @Override
    public SkillBucket prevFor(final SkillBucket bucket) {
        if (!bucket.hasPrevBucket()) {
            return null;
        }
        return SkillBucket.builder()
                .lowerBound(bucket.getLowerBound() - MAX_LEVEL_RANGE)
                .upperBound(bucket.getUpperBound() - MAX_LEVEL_RANGE)
                .build();
    }

    private static SkillBucket bucketFor(final long skillVal) {
        final long lower = (skillVal / MAX_LEVEL_RANGE) * MAX_LEVEL_RANGE;
        final long upper = lower + MAX_LEVEL_RANGE;

        return SkillBucket.builder()
                .lowerBound(lower)
                .upperBound(upper)
                .build();
    }

    private static SkillBucket lowestBucket() {
        return SkillBucket.builder()
                .lowerBound(0)
                .upperBound(MAX_LEVEL_RANGE)
                .build();
    }

    private static boolean isLowestLevel(final long skillValAsLong) {
        return skillValAsLong <= LOWEST_LEVEL;
    }
}
