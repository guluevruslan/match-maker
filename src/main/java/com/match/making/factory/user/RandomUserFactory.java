package com.match.making.factory.user;

import com.match.making.entity.user.User;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUserFactory {
    private static final ThreadLocalRandom LOCAL_RANDOM = ThreadLocalRandom.current();

    private RandomUserFactory() {
    }

    public static User createRandomUser() {
        return User.builder()
                .name(RandomStringUtils.randomAlphabetic(8))
                .latency(LOCAL_RANDOM.nextDouble(1, 200))
                .skill(LOCAL_RANDOM.nextDouble(1, 10))
                .build();
    }
}
