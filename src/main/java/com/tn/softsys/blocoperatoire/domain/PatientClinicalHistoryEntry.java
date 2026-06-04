package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patient_clinical_history", indexes = {
        @Index(name = "idx_patient_clinical_history_patient", columnList = "patient_id"),
        @Index(name = "idx_patient_clinical_history_event_date", columnList = "eventDate")
})
public class PatientClinicalHistoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID entryId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_user_id")
    private User createdBy;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 2000)
    private String summary;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private PatientClinicalHistoryCategory category;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(length = 180)
    private String createdByDisplayName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
