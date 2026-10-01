package com.internship.voting.model;

import com.internship.voting.util.DateFormats;
import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Security audit trail: logins, registrations, votes cast (without the choice) and admin actions. */
@Entity
@Table(name = "audit_logs", indexes = {
        @Index(name = "idx_audit_created", columnList = "created_at")
})
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actor", nullable = false, length = 150)
    private String actor;

    @Column(name = "action", nullable = false, length = 60)
    private String action;

    @Column(name = "details", length = 500)
    private String details;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    protected AuditLog() { }

    public AuditLog(String actor, String action, String details) {
        this.actor = actor;
        this.action = action;
        this.details = details;
        this.createdAt = LocalDateTime.now();
    }

    public String getCreatedAtDisplay() { return DateFormats.format(createdAt); }

    public Long getId() { return id; }
    public String getActor() { return actor; }
    public String getAction() { return action; }
    public String getDetails() { return details; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
