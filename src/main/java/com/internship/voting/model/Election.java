package com.internship.voting.model;

import com.internship.voting.util.DateFormats;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "elections")
public class Election {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 200)
    private String title;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /** Stored status. It is refreshed every minute by ElectionStatusScheduler; business rules use computeStatus(). */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ElectionStatus status = ElectionStatus.UPCOMING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    /**
     * AUTO-STATUS BY TIME: the single source of truth for "is this election open right now?".
     * before start -> UPCOMING, between start and end -> ACTIVE, at/after end -> CLOSED.
     * Being a pure function of (startTime, endTime, now) it is trivial to unit test.
     */
    public ElectionStatus computeStatus(LocalDateTime now) {
        if (now.isBefore(startTime)) {
            return ElectionStatus.UPCOMING;
        }
        if (now.isBefore(endTime)) {
            return ElectionStatus.ACTIVE;
        }
        return ElectionStatus.CLOSED;
    }

    /** Live status for JSP pages (never stale even if the scheduler has not run yet). */
    public ElectionStatus getEffectiveStatus() { return computeStatus(LocalDateTime.now()); }
    public String getStartTimeDisplay() { return DateFormats.format(startTime); }
    public String getEndTimeDisplay() { return DateFormats.format(endTime); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    public ElectionStatus getStatus() { return status; }
    public void setStatus(ElectionStatus status) { this.status = status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
