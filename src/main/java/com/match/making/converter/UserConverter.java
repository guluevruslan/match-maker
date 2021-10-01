package com.match.making.converter;

import com.match.making.entity.payload.UserPayload;
import com.match.making.entity.user.User;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<UserPayload, User> {
    @Override
    public User convert(final UserPayload user) {
        return User.builder()
                .skill(user.getSkill())
                .latency(user.getLatency())
                .name(user.getName())
                .build();
    }
}
