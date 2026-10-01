package com.internship.voting.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.internship.voting.model.Election;
import com.internship.voting.model.ElectionStatus;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

/** Tests the "auto-status by time" rule (Election.computeStatus). */
class ElectionStatusTest {

    private final LocalDateTime now = LocalDateTime.of(2026, 6, 15, 12, 0);

    private Election electionBetween(LocalDateTime start, LocalDateTime end) {
        Election e = new Election();
        e.setStartTime(start);
        e.setEndTime(end);
        return e;
    }

    @Test
    void isUpcoming_beforeStart() {
        Election e = electionBetween(now.plusHours(1), now.plusHours(5));
        assertThat(e.computeStatus(now)).isEqualTo(ElectionStatus.UPCOMING);
    }

    @Test
    void isActive_betweenStartAndEnd() {
        Election e = electionBetween(now.minusHours(1), now.plusHours(1));
        assertThat(e.computeStatus(now)).isEqualTo(ElectionStatus.ACTIVE);
    }

    @Test
    void isActive_exactlyAtStart() {
        Election e = electionBetween(now, now.plusHours(1));
        assertThat(e.computeStatus(now)).isEqualTo(ElectionStatus.ACTIVE);
    }

    @Test
    void isClosed_exactlyAtEnd() {
        Election e = electionBetween(now.minusHours(2), now);
        assertThat(e.computeStatus(now)).isEqualTo(ElectionStatus.CLOSED);
    }

    @Test
    void isClosed_afterEnd() {
        Election e = electionBetween(now.minusDays(2), now.minusDays(1));
        assertThat(e.computeStatus(now)).isEqualTo(ElectionStatus.CLOSED);
    }
}
