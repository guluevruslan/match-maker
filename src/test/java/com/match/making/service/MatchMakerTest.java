package com.match.making.service;

import com.match.making.entity.latency.LatencyType;
import com.match.making.entity.skill.SkillBucket;
import com.match.making.entity.user.User;
import com.match.making.entity.user.UserGroup;
import com.match.making.factory.Factory;
import com.match.making.factory.user.RandomUserFactory;
import com.match.making.storage.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MatchMakerTest {
    private static final User USER_1 = RandomUserFactory.createRandomUser();
    private static final User USER_2 = RandomUserFactory.createRandomUser();
    private static final User USER_3 = RandomUserFactory.createRandomUser();
    private static final User USER_4 = RandomUserFactory.createRandomUser();

    private static final SkillBucket BUCKET_1 = SkillBucket.builder()
            .lowerBound(0)
            .upperBound(2)
            .build();

    private static final SkillBucket BUCKET_2 = SkillBucket.builder()
            .lowerBound(2)
            .upperBound(4)
            .build();

    private static final SkillBucket BUCKET_3 = SkillBucket.builder()
            .lowerBound(4)
            .upperBound(6)
            .build();

    @Mock
    private UserStorage storage;

    @Mock
    private Factory.SkillBucket factory;

    private Match.Maker matchMaker;

    @BeforeEach
    void setUp() {
        matchMaker = new MatchMaker(storage, factory);
    }

    @Test
    void getGroupFromOneBucket() {
        final SortedSet<SkillBucket> buckets = new TreeSet<>(Comparator.comparingLong(SkillBucket::getLowerBound));
        buckets.addAll(List.of(BUCKET_1, BUCKET_2));

        when(storage.buckets())
                .thenReturn(buckets);
        when(storage.latenciesForBucket(any()))
                .thenReturn(new TreeSet<>(List.of(LatencyType.LOW)));

        when(storage.queueSize(BUCKET_1, LatencyType.LOW))
                .thenReturn(4)
                .thenReturn(4)
                .thenReturn(0);

        when(storage.pollUsersFromQueue(BUCKET_1, LatencyType.LOW, 2))
                .thenReturn(List.of(USER_1, USER_2))
                .thenReturn(List.of(USER_3, USER_4));

        final List<UserGroup> groups = matchMaker.makeMatches(2);
        assertEquals(2, groups.size());

        final List<User> users = groups.stream()
                .flatMap(group -> group.getUsers().stream())
                .collect(Collectors.toList());
        assertEquals(4, users.size());

        assertTrue(users.containsAll(List.of(USER_1, USER_2, USER_3, USER_4)));
    }

    @Test
    void groupFromBucketAndNextBucket() {
        final SortedSet<SkillBucket> buckets = new TreeSet<>(Comparator.comparingLong(SkillBucket::getLowerBound));
        buckets.addAll(List.of(BUCKET_1, BUCKET_2));

        when(storage.buckets())
                .thenReturn(buckets);
        when(storage.latenciesForBucket(any()))
                .thenReturn(new TreeSet<>(List.of(LatencyType.LOW)));

        when(storage.queueSize(BUCKET_1, LatencyType.LOW))
                .thenReturn(2)
                .thenReturn(2)
                .thenReturn(2);

        when(factory.nextFor(BUCKET_1))
                .thenReturn(BUCKET_2);

        when(storage.queueSize(BUCKET_2, LatencyType.LOW))
                .thenReturn(1);

        when(storage.pollUsersFromQueue(BUCKET_1, LatencyType.LOW, 2))
                .thenReturn(List.of(USER_1, USER_2));

        when(storage.pollUsersFromQueue(BUCKET_2, LatencyType.LOW, 1))
                .thenReturn(List.of(USER_3));

        final List<UserGroup> groups = matchMaker.makeMatches(3);
        assertEquals(1, groups.size());

        final List<User> users = groups.get(0)
                .getUsers();
        assertEquals(3, users.size());

        assertTrue(users.containsAll(List.of(USER_1, USER_2, USER_3)));
    }

    @Test
    void groupFromBucketNextPrevBucket() {
        final SortedSet<SkillBucket> buckets = new TreeSet<>(Comparator.comparingLong(SkillBucket::getLowerBound));
        buckets.addAll(List.of(BUCKET_2));

        when(storage.buckets())
                .thenReturn(buckets);
        when(storage.latenciesForBucket(any()))
                .thenReturn(new TreeSet<>(List.of(LatencyType.LOW)));

        when(storage.queueSize(BUCKET_2, LatencyType.LOW))
                .thenReturn(2)
                .thenReturn(2)
                .thenReturn(2)
                .thenReturn(2);

        when(factory.nextFor(BUCKET_2))
                .thenReturn(BUCKET_3);

        when(factory.prevFor(BUCKET_2))
                .thenReturn(BUCKET_1);


        when(storage.queueSize(BUCKET_1, LatencyType.LOW))
                .thenReturn(1)
                .thenReturn(1);

        when(storage.queueSize(BUCKET_3, LatencyType.LOW))
                .thenReturn(1)
                .thenReturn(1);

        when(storage.pollUsersFromQueue(BUCKET_1, LatencyType.LOW, 1))
                .thenReturn(List.of(USER_1, USER_2));

        when(storage.pollUsersFromQueue(BUCKET_2, LatencyType.LOW, 2))
                .thenReturn(List.of(USER_3));

        when(storage.pollUsersFromQueue(BUCKET_3, LatencyType.LOW, 1))
                .thenReturn(List.of(USER_4));

        final List<UserGroup> groups = matchMaker.makeMatches(4);
        assertEquals(1, groups.size());

        final List<User> users = groups.get(0)
                .getUsers();
        assertEquals(4, users.size());

        assertTrue(users.containsAll(List.of(USER_1, USER_2, USER_3, USER_4)));
    }
}