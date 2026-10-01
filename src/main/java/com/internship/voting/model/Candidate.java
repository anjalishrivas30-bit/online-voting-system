package com.internship.voting.model;

import jakarta.persistence.*;

@Entity
@Table(name = "candidates", indexes = {
        @Index(name = "idx_candidates_election", columnList = "election_id")
})
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "party", nullable = false, length = 100)
    private String party;

    /** The post the candidate is contesting, e.g. "President". (column is position_title to avoid SQL keyword clashes) */
    @Column(name = "position_title", nullable = false, length = 100)
    private String position;

    @Column(name = "description", length = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "election_id", nullable = false)
    private Election election;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Election getElection() { return election; }
    public void setElection(Election election) { this.election = election; }
}
