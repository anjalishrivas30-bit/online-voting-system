package com.internship.voting.dto;

import com.internship.voting.model.Election;
import java.util.List;

/** Complete counting result of one election: per-candidate votes, winner(s) and turnout. */
public class ElectionResult {

    private final Election election;
    private final List<CandidateResult> candidateResults;
    private final long totalVotes;
    private final long eligibleVoters;
    private final double turnoutPercent;
    private final List<String> winnerNames;
    private final boolean closed;

    public ElectionResult(Election election, List<CandidateResult> candidateResults, long totalVotes,
                          long eligibleVoters, double turnoutPercent, List<String> winnerNames, boolean closed) {
        this.election = election;
        this.candidateResults = candidateResults;
        this.totalVotes = totalVotes;
        this.eligibleVoters = eligibleVoters;
        this.turnoutPercent = turnoutPercent;
        this.winnerNames = winnerNames;
        this.closed = closed;
    }

    public Election getElection() { return election; }
    public List<CandidateResult> getCandidateResults() { return candidateResults; }
    public long getTotalVotes() { return totalVotes; }
    public long getEligibleVoters() { return eligibleVoters; }
    public double getTurnoutPercent() { return turnoutPercent; }
    public List<String> getWinnerNames() { return winnerNames; }
    public boolean isClosed() { return closed; }
    public boolean isWinnerDeclared() { return !winnerNames.isEmpty(); }
    public boolean isTie() { return winnerNames.size() > 1; }
    public String getWinnerText() { return String.join(", ", winnerNames); }
}
