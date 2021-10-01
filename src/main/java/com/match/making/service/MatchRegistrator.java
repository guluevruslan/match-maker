package com.match.making.service;

import com.match.making.entity.user.User;
import com.match.making.storage.UserStorage;

public class MatchRegistrator implements Match.Registrator {
    private final UserStorage storage;

    public MatchRegistrator(final UserStorage storage) {
        this.storage = storage;
    }

    @Override
    public void register(final User user) {
        storage.add(user);
    }
}
