package com.internship.voting.dto;

import com.internship.voting.model.Candidate;

/** One row of the results table. */
public class CandidateResult {

    private final Candidate candidate;
    private final long votes;
    private final double percentage;
    private final boolean winner;

    public CandidateResult(Candidate candidate, long votes, double percentage, boolean winner) {
        this.candidate = candidate;
        this.votes = votes;
        this.percentage = percentage;
        this.winner = winner;
    }

    public Candidate getCandidate() { return candidate; }
    public long getVotes() { return votes; }
    public double getPercentage() { return percentage; }
    public boolean isWinner() { return winner; }
}
