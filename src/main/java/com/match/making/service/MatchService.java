package com.match.making.service;

import com.match.making.entity.user.User;
import com.match.making.entity.user.UserGroup;

import java.util.List;

public class MatchService implements Match.Service {
    private final Match.Maker maker;
    private final Match.Registrator registrator;

    public MatchService(final Match.Maker maker, final Match.Registrator registrator) {
        this.maker = maker;
        this.registrator = registrator;
    }

    @Override
    public void addToMatch(final User user) {
        registrator.register(user);
    }

    @Override
    public List<UserGroup> createMatches(final int groupSize) {
        return maker.makeMatches(groupSize);
    }
}
