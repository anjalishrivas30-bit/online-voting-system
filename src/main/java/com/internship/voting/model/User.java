package com.internship.voting.model;

import com.internship.voting.util.DateFormats;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/** Both admins and voters are rows in the users table; the role column tells them apart. */
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_status", columnList = "status")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "full_name", nullable = false, length = 100)
    private String fullName;

    /** Stored in lower case; used as the login username. */
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    /** Government/college voter id, stored in upper case. Unique. */
    @Column(name = "voter_id", nullable = false, unique = true, length = 30)
    private String voterId;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    /** BCrypt hash - the plain password is never stored. */
    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private VoterStatus status = VoterStatus.PENDING;

    // ---- account lockout fields ----
    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked = false;

    @Column(name = "lock_time")
    private LocalDateTime lockTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public String getCreatedAtDisplay() { return DateFormats.format(createdAt); }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getVoterId() { return voterId; }
    public void setVoterId(String voterId) { this.voterId = voterId; }
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }
    public VoterStatus getStatus() { return status; }
    public void setStatus(VoterStatus status) { this.status = status; }
    public int getFailedAttempts() { return failedAttempts; }
    public void setFailedAttempts(int failedAttempts) { this.failedAttempts = failedAttempts; }
    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }
    public LocalDateTime getLockTime() { return lockTime; }
    public void setLockTime(LocalDateTime lockTime) { this.lockTime = lockTime; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
