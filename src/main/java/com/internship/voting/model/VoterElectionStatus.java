package com.internship.voting.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * The "has_voted" record. It proves that a voter voted in an election without saying for whom.
 * The UNIQUE constraint (voter_id, election_id) is the database-level guarantee of one vote per voter per election,
 * even if two requests arrive at exactly the same moment.
 */
@Entity
@Table(name = "voter_election_status",
        uniqueConstraints = @UniqueConstraint(name = "uk_voter_election", columnNames = {"voter_id", "election_id"}),
        indexes = @Index(name = "idx_ves_election", columnList = "election_id"))
public class VoterElectionStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    @Column(name = "voted_at", nullable = false)
    private LocalDateTime votedAt;

    protected VoterElectionStatus() { }

    public VoterElectionStatus(User voter, Election election) {
        this.voter = voter;
        this.election = election;
        this.votedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public User getVoter() { return voter; }
    public Election getElection() { return election; }
    public LocalDateTime getVotedAt() { return votedAt; }
}
