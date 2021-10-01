package com.match.making.factory;

public interface Factory {
    interface SkillBucket {
        com.match.making.entity.skill.SkillBucket createFrom(double skillVal);

        com.match.making.entity.skill.SkillBucket nextFor(com.match.making.entity.skill.SkillBucket bucket);

        com.match.making.entity.skill.SkillBucket prevFor(com.match.making.entity.skill.SkillBucket bucket);
    }
}
