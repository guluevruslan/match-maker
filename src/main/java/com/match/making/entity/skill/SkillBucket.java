package com.match.making.entity.skill;

import java.util.Objects;

public class SkillBucket {
    private final long lowerBound;
    private final long upperBound;

    private SkillBucket(final long lowerBound, final long upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    public long getLowerBound() {
        return lowerBound;
    }

    public long getUpperBound() {
        return upperBound;
    }

    public boolean hasPrevBucket() {
        return lowerBound > 0;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final SkillBucket that = (SkillBucket) o;
        return lowerBound == that.lowerBound
                && upperBound == that.upperBound;
    }

    @Override
    public int hashCode() {
        return Objects.hash(lowerBound, upperBound);
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        return "SkillBucket{" +
                "lowerBound=" + lowerBound +
                ", upperBound=" + upperBound +
                '}';
    }

    public static class Builder {
        private Long lowerBound;
        private Long upperBound;

        public Builder lowerBound(final long lowerBound) {
            this.lowerBound = lowerBound;
            return this;
        }

        public Builder upperBound(final long upperBound) {
            this.upperBound = upperBound;
            return this;
        }

        public SkillBucket build() {
            Objects.requireNonNull(lowerBound);
            Objects.requireNonNull(upperBound);

            return new SkillBucket(lowerBound, upperBound);
        }
    }
}
