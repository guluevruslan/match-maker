package com.match.making.factory.skill;

import com.match.making.entity.skill.SkillBucket;
import com.match.making.factory.Factory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TwoLevelsDiffSkillBucketFactoryTest {

    private static final Factory.SkillBucket FACTORY = new TwoLevelsDiffSkillBucketFactory();

    @ParameterizedTest
    @MethodSource("skillWithExpectedBucket")
    void checkForValidBucket(final double skillValue, final SkillBucket expectedBucket) {
        final SkillBucket skillBucket = FACTORY.createFrom(skillValue);
        assertEquals(expectedBucket, skillBucket);
    }

    private static Stream<Arguments> skillWithExpectedBucket() {
        return Stream.of(
          Arguments.of(0D, bucketFor(0, 2)),
          Arguments.of(1D, bucketFor(0, 2)),
          Arguments.of(2D, bucketFor(2, 4)),
          Arguments.of(3D, bucketFor(2, 4)),
          Arguments.of(3.49D, bucketFor(2, 4)),
          Arguments.of(3.51D, bucketFor(4, 6)),
          Arguments.of(4D, bucketFor(4, 6)),
          Arguments.of(5D, bucketFor(4, 6)),
          Arguments.of(6D, bucketFor(6, 8)),
          Arguments.of(17D, bucketFor(16, 18))
        );
    }

    private static SkillBucket bucketFor(final long lower, final long upper) {
        return SkillBucket.builder()
                .lowerBound(lower)
                .upperBound(upper)
                .build();
    }

}
