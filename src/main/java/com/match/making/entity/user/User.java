package com.match.making.entity.user;

import java.util.Objects;

public class User {
    private final String name;
    private final double skill;
    private final double latency;
    private final long registrationTime;

    private User(final String name, final double skill, final double latency, final long registrationTime) {
        this.name = name;
        this.skill = skill;
        this.latency = latency;
        this.registrationTime = registrationTime;
    }

    public String getName() {
        return name;
    }

    public double getSkill() {
        return skill;
    }

    public double getLatency() {
        return latency;
    }

    public long getRegistrationTime() {
        return registrationTime;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String name;
        private Double skill;
        private Double latency;

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder skill(final double skill) {
            this.skill = skill;
            return this;
        }

        public Builder latency(final double latency) {
            this.latency = latency;
            return this;
        }

        public User build() {
            Objects.requireNonNull(name, "Name can't be null");
            Objects.requireNonNull(skill, "Skill can't be null");
            Objects.requireNonNull(latency, "Latency can't be null");

            return new User(name, skill, latency, System.nanoTime());
        }
    }
}
