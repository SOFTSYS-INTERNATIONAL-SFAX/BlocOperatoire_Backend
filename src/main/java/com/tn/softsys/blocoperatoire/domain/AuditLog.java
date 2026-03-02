package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user", columnList = "user_id"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
                @Index(name = "idx_audit_action", columnList = "action")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    @ManyToOne(optional = true)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String action;
    // ex: CREATE_PATIENT, DELETE_INTERVENTION, LOGIN_SUCCESS

    @Column(nullable = false)
    private String module;
    // ex: PATIENT, INTERVENTION, AUTHENTICATION

    @Column(nullable = false)
    private LocalDateTime timestamp;

    private String ipAddress;

    @Column(length = 2000)
    private String details;

    @PrePersist
    public void prePersist() {
        timestamp = LocalDateTime.now();
    }

    /*
     * IMPORTANT :
     * On empêche toute mise à jour.
     */
    @PreUpdate
    public void preventUpdate() {
        throw new UnsupportedOperationException("AuditLog is immutable and cannot be updated");
    }
}
