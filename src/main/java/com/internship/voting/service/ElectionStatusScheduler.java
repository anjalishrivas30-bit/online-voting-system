package com.internship.voting.service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/** Runs every minute and moves elections UPCOMING -> ACTIVE -> CLOSED according to their start/end times. */
@Component
public class ElectionStatusScheduler {

    private final ElectionService electionService;

    public ElectionStatusScheduler(ElectionService electionService) {
        this.electionService = electionService;
    }

    @Scheduled(fixedRate = 60_000, initialDelay = 10_000)
    public void updateStatuses() {
        electionService.refreshStatuses();
    }
}
