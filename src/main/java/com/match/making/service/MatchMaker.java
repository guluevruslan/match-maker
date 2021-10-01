package com.match.making.service;

import com.match.making.entity.latency.LatencyType;
import com.match.making.entity.skill.SkillBucket;
import com.match.making.entity.user.User;
import com.match.making.entity.user.UserGroup;
import com.match.making.factory.Factory;
import com.match.making.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MatchMaker implements Match.Maker {
    private final UserStorage storage;
    private final Factory.SkillBucket skillBucketFactory;

    public MatchMaker(final UserStorage storage, final Factory.SkillBucket skillBucketFactory) {
        this.storage = storage;
        this.skillBucketFactory = skillBucketFactory;
    }

    @Override
    public List<UserGroup> makeMatches(final int groupSize) {
        return storage.buckets()
                .stream()
                .flatMap(bucket -> groupsForBucket(bucket, groupSize).stream())
                .collect(Collectors.toList());
    }

    private List<UserGroup> groupsForBucket(final SkillBucket bucket,
                                            final int groupSize) {
        return storage.latenciesForBucket(bucket)
                .stream()
                .flatMap(latency -> groupsForLatency(bucket, latency, groupSize).stream())
                .collect(Collectors.toList());
    }

    private List<UserGroup> groupsForLatency(final SkillBucket skillBucket,
                                             final LatencyType latencyType,
                                             final int groupSize) {
        final List<UserGroup> groupsOnlyFromCurrentBucket
                = tryToMakeGroupsFromCurrentBucket(skillBucket, latencyType, groupSize);
        final List<UserGroup> groupsFromCurrentAndNeighborBucket
                = tryToMakeGroupsFromCurrentAndNeighborBuckets(skillBucket, latencyType, groupSize);

        return combineCollections(groupsOnlyFromCurrentBucket, groupsFromCurrentAndNeighborBucket);
    }

    private List<UserGroup> tryToMakeGroupsFromCurrentBucket(final SkillBucket skillBucket,
                                                             final LatencyType latencyType,
                                                             final int groupSize) {
        final int queueSize = storage.queueSize(skillBucket, latencyType);

        if (queueSize >= groupSize) {
            return makeGroupsForQueue(skillBucket, latencyType, groupSize);
        } else {
            return List.of();
        }
    }

    private List<UserGroup> makeGroupsForQueue(final SkillBucket skillBucket,
                                               final LatencyType latencyType,
                                               final int groupSize) {
        final List<UserGroup> groups = new ArrayList<>();
        final int queueSize = storage.queueSize(skillBucket, latencyType);
        final int groupsCount = queueSize / groupSize;

        for (int i = 0; i < groupsCount; i++) {
            final List<User> users = storage.pollUsersFromQueue(skillBucket, latencyType, groupSize);
            groups.add(UserGroup.of(users));
        }

        return groups;
    }

    private List<UserGroup> tryToMakeGroupsFromCurrentAndNeighborBuckets(final SkillBucket skillBucket,
                                                                         final LatencyType latencyType,
                                                                         final int groupSize) {
        final int queueSize = storage.queueSize(skillBucket, latencyType);

        if (queueSize == 0) {
            return List.of();
        }

        final List<UserGroup> groupFromCurrentAndNextBucket
                = tryToMakeGroupsFromCurrentAndNextBucket(skillBucket, latencyType, groupSize);
        final List<UserGroup> groupFromCurrentNextAndPrevAndBucket
                = tryToMakeGroupsFromCurrentNextAndPrevBucket(skillBucket, latencyType, groupSize);

        return combineCollections(groupFromCurrentAndNextBucket, groupFromCurrentNextAndPrevAndBucket);
    }

    private List<UserGroup> tryToMakeGroupsFromCurrentAndNextBucket(final SkillBucket skillBucket,
                                                                    final LatencyType latencyType,
                                                                    final int groupSize) {
        final int queueSize = storage.queueSize(skillBucket, latencyType);

        final SkillBucket nextSkillBucket = skillBucketFactory.nextFor(skillBucket);
        final int nextQueueSize = storage.queueSize(nextSkillBucket, latencyType);

        if (queueSize + nextQueueSize >= groupSize) {
            final List<User> users = combineCollections(
                    storage.pollUsersFromQueue(skillBucket, latencyType, queueSize),
                    storage.pollUsersFromQueue(nextSkillBucket, latencyType, (groupSize - queueSize)));
            return List.of(UserGroup.of(users));
        } else {
            return List.of();
        }
    }

    private List<UserGroup> tryToMakeGroupsFromCurrentNextAndPrevBucket(final SkillBucket skillBucket,
                                                                        final LatencyType latencyType,
                                                                        final int groupSize) {
        final SkillBucket prevSkillBucket = skillBucketFactory.prevFor(skillBucket);
        if (prevSkillBucket == null) {
            return List.of();
        }

        final int queueSize = storage.queueSize(skillBucket, latencyType);

        final SkillBucket nextSkillBucket = skillBucketFactory.nextFor(skillBucket);
        final int nextQueueSize = storage.queueSize(nextSkillBucket, latencyType);

        final int prevQueueSize = storage.queueSize(prevSkillBucket, latencyType);

        if (prevQueueSize + nextQueueSize + queueSize >= groupSize) {
            final List<User> users = combineCollections(
                    storage.pollUsersFromQueue(skillBucket, latencyType, queueSize),
                    pollElementsFromNextAndPrevQueue(
                            prevSkillBucket,
                            nextSkillBucket,
                            latencyType,
                            (groupSize - queueSize)));
            return List.of(UserGroup.of(users));
        } else {
            return List.of();
        }
    }

    private List<User> pollElementsFromNextAndPrevQueue(final SkillBucket prevSkillBucket,
                                                        final SkillBucket nextSkillBucket,
                                                        final LatencyType latencyType,
                                                        final int count) {
        final int prevQueueSize = storage.queueSize(prevSkillBucket, latencyType);
        final int nextQueueSize = storage.queueSize(nextSkillBucket, latencyType);
        if (prevQueueSize > nextQueueSize) {
            final int limit = Math.min(count, prevQueueSize);
            return combineCollections(
                    storage.pollUsersFromQueue(prevSkillBucket, latencyType, limit),
                    storage.pollUsersFromQueue(nextSkillBucket, latencyType, (count - limit))
            );
        } else {
            final int limit = Math.min(count, nextQueueSize);
            return combineCollections(
                    storage.pollUsersFromQueue(nextSkillBucket, latencyType, limit),
                    storage.pollUsersFromQueue(prevSkillBucket, latencyType, (count - limit))
            );
        }
    }

    private static <T> List<T> combineCollections(final List<T> one, final List<T> another) {
        final List<T> result = new ArrayList<>(one);
        result.addAll(another);
        return result;
    }
}
