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
@Table(
        name = "alerts",
        indexes = {
                @Index(name = "idx_alert_type", columnList = "type"),
                @Index(name = "idx_alert_severity", columnList = "severity"),
                @Index(name = "idx_alert_active", columnList = "active"),
                @Index(name = "idx_alert_viewed", columnList = "viewed"),
                @Index(name = "idx_alert_created_at", columnList = "createdAt")
        }
)
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID alertId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 60)
    private AlertType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AlertSeverity severity;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(name = "title_en", length = 200)
    private String titleEn;

    @Column(name = "title_ar", length = 200)
    private String titleAr;

    @Column(nullable = false, length = 2000)
    private String message;

    @Column(name = "message_en", length = 2000)
    private String messageEn;

    @Column(name = "message_ar", length = 2000)
    private String messageAr;

    @Column(name = "patient_label", length = 200)
    private String patientLabel;

    @Column(name = "patient_label_en", length = 200)
    private String patientLabelEn;

    @Column(name = "patient_label_ar", length = 200)
    private String patientLabelAr;

    @Column(name = "intervention_label", length = 512)
    private String interventionLabel;

    @Column(name = "intervention_label_en", length = 512)
    private String interventionLabelEn;

    @Column(name = "intervention_label_ar", length = 512)
    private String interventionLabelAr;

    @Column(name = "room_label", length = 255)
    private String roomLabel;

    @Column(name = "room_label_en", length = 255)
    private String roomLabelEn;

    @Column(name = "room_label_ar", length = 255)
    private String roomLabelAr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acknowledged_by")
    private User acknowledgedBy;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @Builder.Default
    @Column(nullable = false)
    private Boolean acknowledged = false;

    @Builder.Default
    @Column(nullable = false)
    private Boolean viewed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime acknowledgedAt;
    private LocalDateTime viewedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();

        if (active == null) {
            active = true;
        }

        if (acknowledged == null) {
            acknowledged = false;
        }

        if (viewed == null) {
            viewed = false;
        }
    }
}
