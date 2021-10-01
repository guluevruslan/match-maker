package com.match.making.service.print;

import com.match.making.entity.statistics.UserGroupStatistics;
import com.match.making.entity.user.User;
import com.match.making.entity.user.UserGroup;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserGroupPrinterService implements UserGroupPrinter {
    @Override
    public void printUserGroup(final UserGroup group) {
        final List<User> users = group.getUsers();
        final long time = System.nanoTime();
        users.stream()
                .map(user -> UserGroupStatistics.of(user, time))
                .reduce(UserGroupStatistics::merge)
                .ifPresent(stats -> print(stats, group));
    }

    private static void print(final UserGroupStatistics statistics, final UserGroup userGroup) {
        final List<User> users = userGroup.getUsers();
        final int size = users.size();
        final String format = "[min = %s max = %s avg = %s]";
        final List<String> names = users.stream().map(User::getName).collect(Collectors.toList());
        final StringBuilder stringBuilder =
                new StringBuilder("-------------------------------------------------------")
                        .append("\n")
                        .append("GROUP: ").append(userGroup.getId())
                        .append("\n")
                        .append("SKILL:")
                        .append(String.format(format, statistics.getMinSkill(), statistics.getMaxSkill(), statistics.getAvgSkill(size)))
                        .append("\n")
                        .append("LATENCY:")
                        .append(String.format(format, statistics.getMinLatency(), statistics.getMaxLatency(), statistics.getAvgLatency(size)))
                        .append("\n")
                        .append("TIME TO WAIT:")
                        .append(String.format(format, statistics.getMinTime(), statistics.getMaxTime(), statistics.getAvgTime(size)))
                        .append("\n")
                        .append("NAMES:").append(names)
                        .append("\n")
                        .append("-------------------------------------------------------")
                        .append("\n");

        System.out.println(stringBuilder);
    }
}
