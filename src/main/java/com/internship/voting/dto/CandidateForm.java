package com.internship.voting.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CandidateForm {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Party is required")
    @Size(max = 100, message = "Party must be at most 100 characters")
    private String party;

    /** Optional. If left empty the service stores "Candidate". */
    @Size(max = 100, message = "Position must be at most 100 characters")
    private String position;

    @Size(max = 1000, message = "Description must be at most 1000 characters")
    private String description;

    @NotNull(message = "Select an election")
    private Long electionId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getParty() { return party; }
    public void setParty(String party) { this.party = party; }
    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getElectionId() { return electionId; }
    public void setElectionId(Long electionId) { this.electionId = electionId; }
}
