package com.match.making.entity.payload;

public class UserPayload {
    private String name;
    private Double skill;
    private Double latency;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Double getSkill() {
        return skill;
    }

    public void setSkill(final Double skill) {
        this.skill = skill;
    }

    public Double getLatency() {
        return latency;
    }

    public void setLatency(final Double latency) {
        this.latency = latency;
    }

    @Override
    public String toString() {
        return "UserPayload{" +
                "name='" + name + '\'' +
                ", skill=" + skill +
                ", latency=" + latency +
                '}';
    }
}
