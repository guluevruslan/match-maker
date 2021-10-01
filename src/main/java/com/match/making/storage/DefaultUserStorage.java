package com.match.making.storage;

import com.match.making.entity.latency.LatencyType;
import com.match.making.entity.skill.SkillBucket;
import com.match.making.entity.user.User;
import com.match.making.factory.Factory;
import com.match.making.factory.skill.TwoLevelsDiffSkillBucketFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.stream.Collectors;

public class DefaultUserStorage implements UserStorage {
    private static final int INITIAL_CAPACITY = 150;
    private final Map<SkillBucket, Map<LatencyType, Queue<User>>> storage = new ConcurrentHashMap<>();
    private final Factory.SkillBucket skillBucketFactory;

    public DefaultUserStorage(final Factory.SkillBucket skillBucketFactory) {
        this.skillBucketFactory = skillBucketFactory;
    }

    public static UserStorage init() {
        return new DefaultUserStorage(new TwoLevelsDiffSkillBucketFactory());
    }

    @Override
    public SortedSet<SkillBucket> buckets() {
        return storage.keySet()
                .stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<>(Comparator.comparingLong(SkillBucket::getLowerBound))
                ));
    }

    @Override
    public SortedSet<LatencyType> latenciesForBucket(final SkillBucket bucket) {
        return Optional.ofNullable(storage.get(bucket))
                .map(map -> new TreeSet<>(map.keySet()))
                .orElse(new TreeSet<>());
    }

    @Override
    public int queueSize(final SkillBucket bucket, final LatencyType latencyType) {
        return Optional.ofNullable(storage.get(bucket))
                .map(map -> map.get(latencyType))
                .map(Queue::size)
                .orElse(0);
    }

    @Override
    public List<User> pollUsersFromQueue(final SkillBucket bucket, final LatencyType latencyType, final int count) {
        return Optional.ofNullable(storage.get(bucket))
                .map(map -> map.get(latencyType))
                .map(queue -> pollElementsFromQueue(queue, count))
                .orElse(List.of());
    }

    private static List<User> pollElementsFromQueue(final Queue<User> userQueue, final int count) {
        final List<User> users = new ArrayList<>();
        final int usersAmount = Math.min(userQueue.size(), count);
        for (int i = 0; i < usersAmount; i++) {
            users.add(userQueue.poll());
        }
        return users;
    }

    @Override
    public void add(final User user) {
        final SkillBucket skillBucket = skillBucketFactory.createFrom(user.getSkill());
        storage.compute(skillBucket, (k, v) -> addUser(v, user));
    }

    private static Map<LatencyType, Queue<User>> addUser(final Map<LatencyType, Queue<User>> userQueueForLatency,
                                                         final User user) {
        final LatencyType latencyType = LatencyType.defineLatencyType(user.getLatency());
        if (userQueueForLatency == null) {
            return createNewUserQueueForLatency(user, latencyType);
        }

        final boolean isNoQueueForLatency = !userQueueForLatency.containsKey(latencyType);
        if (isNoQueueForLatency) {
            final Queue<User> userQueue = createQueueForUser(user);
            userQueueForLatency.put(latencyType, userQueue);
        } else {
            final Queue<User> userQueue = userQueueForLatency.get(latencyType);
            userQueue.add(user);
        }
        return userQueueForLatency;
    }

    private static Map<LatencyType, Queue<User>> createNewUserQueueForLatency(final User user,
                                                                              final LatencyType latencyType) {
        final Map<LatencyType, Queue<User>> result = new ConcurrentHashMap<>();
        final Queue<User> userQueue = createUserQueue();
        userQueue.add(user);
        result.put(latencyType, userQueue);
        return result;
    }

    private static Queue<User> createQueueForUser(final User user) {
        final Queue<User> userQueue = createUserQueue();
        userQueue.add(user);
        return userQueue;
    }

    private static Queue<User> createUserQueue() {
        return new PriorityBlockingQueue<>(INITIAL_CAPACITY, Comparator.comparing(User::getRegistrationTime));
    }
}
