package com.internship.voting.model;

/** Registration state of a voter. Only APPROVED voters may cast votes. */
public enum VoterStatus {
    PENDING, APPROVED, REJECTED
}
