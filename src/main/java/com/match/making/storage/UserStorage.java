package com.match.making.storage;

import com.match.making.entity.latency.LatencyType;
import com.match.making.entity.skill.SkillBucket;
import com.match.making.entity.user.User;

import java.util.List;
import java.util.SortedSet;

public interface UserStorage {
    void add(User user);

    SortedSet<SkillBucket> buckets();

    SortedSet<LatencyType> latenciesForBucket(SkillBucket bucket);

    int queueSize(SkillBucket bucket, LatencyType latencyType);

    List<User> pollUsersFromQueue(SkillBucket bucket, LatencyType latencyType, int count);
}
