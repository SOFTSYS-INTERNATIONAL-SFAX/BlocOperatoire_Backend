package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter(AccessLevel.NONE) // 🔒 immutabilité contrôlée
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "audit_logs",
        indexes = {
                @Index(name = "idx_audit_user", columnList = "user_id"),
                @Index(name = "idx_audit_timestamp", columnList = "timestamp"),
                @Index(name = "idx_audit_action", columnList = "action"),
                @Index(name = "idx_audit_module", columnList = "module"),
                @Index(name = "idx_audit_reference", columnList = "reference_id")
        }
)
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID logId;

    /* ================= USER ================= */

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

    /* ================= ACTION ================= */

    @Column(nullable = false, length = 100)
    private String action;

    /* ================= MODULE ================= */

    @Column(nullable = false, length = 100)
    private String module;

    /* ================= BUSINESS REFERENCE ================= */

    @Column(name = "reference_id")
    private UUID referenceId; // scoreId / interventionId / patientId

    /* ================= TIMESTAMP ================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime timestamp;

    /* ================= IP ================= */

    @Column(length = 45)
    private String ipAddress;

    /* ================= DETAILS ================= */

    @Column(columnDefinition = "TEXT")
    private String details;

    /* ================= INTEGRITY HASH (OPTIONNEL MAIS RECOMMANDÉ) ================= */

    @Column(length = 64)
    private String integrityHash;

    /* ================= LIFECYCLE ================= */

    @PrePersist
    public void prePersist() {

        this.timestamp = LocalDateTime.now();

        // Hash simple d'intégrité
        this.integrityHash = generateIntegrityHash();
    }

    @PreUpdate
    public void preventUpdate() {
        throw new UnsupportedOperationException(
                "AuditLog is immutable and cannot be updated"
        );
    }

    @PreRemove
    public void preventDelete() {
        throw new UnsupportedOperationException(
                "AuditLog cannot be deleted"
        );
    }

    /* ================= PRIVATE ================= */

    private String generateIntegrityHash() {

        String base = String.valueOf(timestamp)
                + action
                + module
                + String.valueOf(referenceId)
                + String.valueOf(details);

        return Integer.toHexString(base.hashCode());
    }
}