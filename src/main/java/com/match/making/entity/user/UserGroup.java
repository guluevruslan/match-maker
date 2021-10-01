package com.match.making.entity.user;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class UserGroup {
    private final static AtomicLong INDEX = new AtomicLong(0);
    private final List<User> users;
    private final long id;

    private UserGroup(final List<User> users) {
        this.users = users;
        id = INDEX.incrementAndGet();
    }

    public static UserGroup of(final List<User> users) {
        return new UserGroup(new ArrayList<>(users));
    }

    public List<User> getUsers() {
        return new ArrayList<>(users);
    }

    public long getId() {
        return id;
    }
}
