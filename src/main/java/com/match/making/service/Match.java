package com.match.making.service;

import com.match.making.entity.user.User;
import com.match.making.entity.user.UserGroup;

import java.util.List;

public interface Match {
    interface Maker {
        List<UserGroup> makeMatches(int groupSize);
    }

    interface Registrator {
        void register(User user);
    }

    interface Service {
        void addToMatch(User user);

        List<UserGroup> createMatches(int groupSize);
    }
}
