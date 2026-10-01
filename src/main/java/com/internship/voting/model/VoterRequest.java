package com.internship.voting.model;

import com.internship.voting.util.DateFormats;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * A request submitted by a voter (profile correction, information update, other).
 * One voter has many requests (many-to-one to users). An admin approves or rejects it and may add a remark.
 */
@Entity
@Table(name = "voter_requests", indexes = {
        @Index(name = "idx_requests_status", columnList = "status"),
        @Index(name = "idx_requests_voter", columnList = "voter_id")
})
public class VoterRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_type", nullable = false, length = 40)
    private RequestType type;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private RequestStatus status = RequestStatus.PENDING;

    @Column(name = "admin_remark", length = 500)
    private String adminRemark;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    /**
     * In-app notification flag. false = the admin has decided and the voter has not seen the result yet
     * (shown as a message on the voter dashboard and a badge in the sidebar).
     */
    @Column(name = "voter_notified", nullable = false)
    private boolean voterNotified = true;

    protected VoterRequest() { }

    public VoterRequest(User voter, RequestType type, String description) {
        this.voter = voter;
        this.type = type;
        this.description = description;
        this.status = RequestStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.voterNotified = true;
    }

    public String getCreatedAtDisplay() { return DateFormats.format(createdAt); }
    public String getDecidedAtDisplay() { return DateFormats.format(decidedAt); }

    public Long getId() { return id; }
    public User getVoter() { return voter; }
    public void setVoter(User voter) { this.voter = voter; }
    public RequestType getType() { return type; }
    public void setType(RequestType type) { this.type = type; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public RequestStatus getStatus() { return status; }
    public void setStatus(RequestStatus status) { this.status = status; }
    public String getAdminRemark() { return adminRemark; }
    public void setAdminRemark(String adminRemark) { this.adminRemark = adminRemark; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getDecidedAt() { return decidedAt; }
    public void setDecidedAt(LocalDateTime decidedAt) { this.decidedAt = decidedAt; }
    public boolean isVoterNotified() { return voterNotified; }
    public void setVoterNotified(boolean voterNotified) { this.voterNotified = voterNotified; }
}
