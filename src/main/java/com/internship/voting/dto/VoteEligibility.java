package com.internship.voting.dto;

/** The result of the "smart eligibility check" for one voter and one election. */
public enum VoteEligibility {
    CAN_VOTE("Vote Now"),
    NOT_STARTED("Voting has not started yet."),
    ENDED("Voting has ended."),
    ALREADY_VOTED("You have already voted."),
    NOT_APPROVED("Your registration must be approved before you can vote.");

    private final String message;

    VoteEligibility(String message) { this.message = message; }

    public String getMessage() { return message; }
}
