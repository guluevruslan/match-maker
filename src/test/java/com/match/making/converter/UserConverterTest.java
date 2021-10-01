package com.match.making.converter;

import com.match.making.entity.payload.UserPayload;
import com.match.making.entity.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.core.convert.converter.Converter;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserConverterTest {
    private static final Converter<UserPayload, User> CONVERTER = new UserConverter();

    private static final String NAME = "NAME";
    private static final double SKILL = 1.22;
    private static final double LATENCY = 23.45;

    @Test
    void checkValidConversion() {
        final long currentTime = System.nanoTime();
        final UserPayload userPayload = new UserPayload();
        userPayload.setName(NAME);
        userPayload.setSkill(SKILL);
        userPayload.setLatency(LATENCY);

        final User user = CONVERTER.convert(userPayload);

        assertNotNull(user);
        assertEquals(NAME, user.getName());
        assertEquals(SKILL, user.getSkill());
        assertEquals(LATENCY, user.getLatency());
        assertTrue(currentTime <= user.getRegistrationTime());
    }

    @ParameterizedTest
    @MethodSource("userPayload")
    void checkNpeOnNullableFields(final UserPayload userPayload) {
        assertThrows(NullPointerException.class, () -> CONVERTER.convert(userPayload));
    }

    private static Stream<Arguments> userPayload() {
        return Stream.of(
                Arguments.of(noNameUser()),
                Arguments.of(noSkillUser()),
                Arguments.of(noLatencyUser())
        );
    }

    private static UserPayload noNameUser() {
        final UserPayload userPayload = new UserPayload();
        userPayload.setSkill(SKILL);
        userPayload.setLatency(LATENCY);
        return userPayload;
    }

    private static UserPayload noSkillUser() {
        final UserPayload userPayload = new UserPayload();
        userPayload.setName(NAME);
        userPayload.setLatency(LATENCY);
        return userPayload;
    }

    private static UserPayload noLatencyUser() {
        final UserPayload userPayload = new UserPayload();
        userPayload.setName(NAME);
        userPayload.setSkill(SKILL);
        return userPayload;
    }
}