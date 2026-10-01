package com.internship.voting.dto;

import com.internship.voting.model.Election;

/** An election plus the logged-in voter's eligibility for it (used on the voter dashboard and elections page). */
public class VoterElectionView {

    private final Election election;
    private final boolean voted;
    private final VoteEligibility eligibility;

    public VoterElectionView(Election election, boolean voted, VoteEligibility eligibility) {
        this.election = election;
        this.voted = voted;
        this.eligibility = eligibility;
    }

    public Election getElection() { return election; }
    public boolean isVoted() { return voted; }
    public VoteEligibility getEligibility() { return eligibility; }
    public String getMessage() { return eligibility.getMessage(); }
    public boolean isCanVote() { return eligibility == VoteEligibility.CAN_VOTE; }
}
