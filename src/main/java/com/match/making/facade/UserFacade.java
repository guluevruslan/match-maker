package com.match.making.facade;

import com.match.making.entity.payload.UserPayload;
import com.match.making.entity.user.User;
import com.match.making.factory.user.RandomUserFactory;
import com.match.making.service.Match;
import org.springframework.core.convert.converter.Converter;

public class UserFacade {
    private final Match.Service matchService;
    private final Converter<UserPayload, User> userConverter;

    public UserFacade(final Match.Service matchService, final Converter<UserPayload, User> userConverter) {
        this.matchService = matchService;
        this.userConverter = userConverter;
    }

    public void registerUser(final UserPayload userPayload) {
        final User user = userConverter.convert(userPayload);
        matchService.addToMatch(user);
    }

    public void registerGeneratedUsers(final int count) {
        for (int i = 0; i < count; i++) {
            final User user = RandomUserFactory.createRandomUser();
            matchService.addToMatch(user);
        }
    }
}
