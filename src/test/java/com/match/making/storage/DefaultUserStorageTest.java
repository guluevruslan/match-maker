package com.match.making.storage;

import com.match.making.entity.latency.LatencyType;
import com.match.making.entity.skill.SkillBucket;
import com.match.making.entity.user.User;
import com.match.making.factory.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.SortedSet;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultUserStorageTest {
    private final double SKILL_1 = 1.11;
    private final double SKILL_2 = 2.02;
    private final double SKILL_3 = 4.54;

    private final SkillBucket BUCKET_1 = SkillBucket.builder()
            .lowerBound(0)
            .upperBound(3)
            .build();

    private final SkillBucket BUCKET_2 = SkillBucket.builder()
            .lowerBound(3)
            .upperBound(6)
            .build();

    @Mock
    private Factory.SkillBucket factory;

    private UserStorage userStorage;

    @BeforeEach
    void setUp() {
        userStorage = new DefaultUserStorage(factory);
    }

    @Test
    void checkAdd() {
        when(factory.createFrom(SKILL_1))
                .thenReturn(BUCKET_1);
        final int initialSize = userStorage.queueSize(BUCKET_1, LatencyType.LOW);
        assertEquals(0, initialSize);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_1)
                .latency(42)
                .build());

        final int sizeAfterAdd = userStorage.queueSize(BUCKET_1, LatencyType.LOW);
        assertEquals(1, sizeAfterAdd);
    }

    @Test
    void checkBuckets() {
        when(factory.createFrom(SKILL_1))
                .thenReturn(BUCKET_1);
        when(factory.createFrom(SKILL_3))
                .thenReturn(BUCKET_2);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_1)
                .latency(42)
                .build());

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_3)
                .latency(42)
                .build());

        final SortedSet<SkillBucket> buckets = userStorage.buckets();
        assertEquals(2, buckets.size());
        assertTrue(buckets.containsAll(List.of(BUCKET_1, BUCKET_2)));
    }

    @Test
    void checkLatencies() {
        assertTrue(userStorage.latenciesForBucket(BUCKET_2).isEmpty());

        when(factory.createFrom(SKILL_1))
                .thenReturn(BUCKET_1);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_1)
                .latency(42)
                .build());

        when(factory.createFrom(SKILL_2))
                .thenReturn(BUCKET_1);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_2)
                .latency(99)
                .build());

        final SortedSet<LatencyType> latencyTypes = userStorage.latenciesForBucket(BUCKET_1);
        assertEquals(2, latencyTypes.size());
        assertTrue(latencyTypes.containsAll(List.of(LatencyType.LOW, LatencyType.MEDIUM)));
    }

    @Test
    void checkQueueSize() {
        assertEquals(0, userStorage.queueSize(BUCKET_2, LatencyType.LOW));

        when(factory.createFrom(SKILL_1))
                .thenReturn(BUCKET_1);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_1)
                .latency(42)
                .build());

        when(factory.createFrom(SKILL_2))
                .thenReturn(BUCKET_1);

        userStorage.add(User.builder()
                .name("name")
                .skill(SKILL_2)
                .latency(2)
                .build());

        final int queueSize = userStorage.queueSize(BUCKET_1, LatencyType.LOW);
        assertEquals(2, queueSize);
    }

    @Test
    void checkPollFromQueue() {
        assertTrue(userStorage.pollUsersFromQueue(BUCKET_2, LatencyType.LOW, 1).isEmpty());

        when(factory.createFrom(SKILL_1))
                .thenReturn(BUCKET_1);

        final User user1 = User.builder()
                .name("name")
                .skill(SKILL_1)
                .latency(42)
                .build();
        userStorage.add(user1);

        when(factory.createFrom(SKILL_2))
                .thenReturn(BUCKET_1);

        final User user2 = User.builder()
                .name("name")
                .skill(SKILL_2)
                .latency(2)
                .build();
        userStorage.add(user2);

        final List<User> users = userStorage.pollUsersFromQueue(BUCKET_1, LatencyType.LOW, 3);
        assertEquals(2, users.size());
        assertTrue(users.containsAll(List.of(user1, user2)));
    }
}