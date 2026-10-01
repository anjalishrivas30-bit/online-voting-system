package com.internship.voting.model;

/** Life cycle of a voter request: PENDING -> APPROVED or REJECTED (decided by an admin). */
public enum RequestStatus {
    PENDING("Pending"), APPROVED("Approved"), REJECTED("Rejected");

    private final String label;

    RequestStatus(String label) { this.label = label; }

    public String getLabel() { return label; }
}
