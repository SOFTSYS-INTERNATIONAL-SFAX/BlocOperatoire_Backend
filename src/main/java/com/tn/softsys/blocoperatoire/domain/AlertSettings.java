package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "alert_settings")
public class AlertSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID settingsId;

    @Column(nullable = false)
    private Integer sspiThresholdMinutes;

    @Column(nullable = false)
    private Boolean soundEnabled;

    @Column(nullable = false)
    private Integer escalationLevel1Minutes;

    @Column(nullable = false)
    private Integer escalationLevel2Minutes;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (sspiThresholdMinutes == null || sspiThresholdMinutes < 1) {
            sspiThresholdMinutes = 2;
        }

        if (soundEnabled == null) {
            soundEnabled = true;
        }

        if (escalationLevel1Minutes == null || escalationLevel1Minutes <= sspiThresholdMinutes) {
            escalationLevel1Minutes = 5;
        }

        if (escalationLevel2Minutes == null || escalationLevel2Minutes <= escalationLevel1Minutes) {
            escalationLevel2Minutes = 10;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
