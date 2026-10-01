package com.internship.voting.model;

/** What kind of request a voter is submitting. */
public enum RequestType {
    PROFILE_CORRECTION("Profile Correction"),
    VOTER_INFO_UPDATE("Voter Information Update"),
    OTHER("Other Election-related Request");

    private final String label;

    RequestType(String label) { this.label = label; }

    public String getLabel() { return label; }
}
