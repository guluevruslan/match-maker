package com.match.making.worker;

import com.match.making.service.Match;
import com.match.making.service.print.UserGroupPrinter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class MatchScheduledWorker implements Runnable {
    private final Match.Service matchService;
    private final UserGroupPrinter printer;
    private final int groupSize;

    public MatchScheduledWorker(final Match.Service matchService,
                                final UserGroupPrinter printer,
                                @Value("${application.match.groupSize}") final int groupSize) {
        this.matchService = matchService;
        this.printer = printer;
        this.groupSize = groupSize;
    }


    @Async
    @Override
    @Scheduled(initialDelay = 1, fixedRate = 1, timeUnit = TimeUnit.MINUTES)
    public void run() {
        matchService.createMatches(groupSize)
                .forEach(printer::printUserGroup);
    }
}
