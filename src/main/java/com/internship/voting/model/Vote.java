package com.internship.voting.model;

import jakarta.persistence.*;

/**
 * BALLOT SECRECY: a Vote row knows the election and the chosen candidate - and NOTHING about the voter.
 * There is also no timestamp, so a vote cannot be matched to a voter by comparing times with
 * voter_election_status.voted_at. Who voted is stored separately; what they chose is stored here.
 */
@Entity
@Table(name = "votes", indexes = {
        @Index(name = "idx_votes_election", columnList = "election_id"),
        @Index(name = "idx_votes_candidate", columnList = "candidate_id")
})
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "candidate_id", nullable = false)
    private Candidate candidate;

    protected Vote() { }

    public Vote(Election election, Candidate candidate) {
        this.election = election;
        this.candidate = candidate;
    }

    public Long getId() { return id; }
    public Election getElection() { return election; }
    public Candidate getCandidate() { return candidate; }
}
