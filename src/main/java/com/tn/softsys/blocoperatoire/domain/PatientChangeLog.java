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
@Table(name = "patient_change_logs", indexes = {
        @Index(name = "idx_patient_change_patient", columnList = "patientId"),
        @Index(name = "idx_patient_change_date", columnList = "changedAt")
})
public class PatientChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID patientId;

    @Column(nullable = false, length = 120)
    private String fieldName;

    @Column(length = 2000)
    private String oldValue;

    @Column(length = 2000)
    private String newValue;

    @Column(nullable = false)
    private LocalDateTime changedAt;

    private UUID changedByUserId;

    private String changedByDisplayName;

    @PrePersist
    public void prePersist() {
        changedAt = LocalDateTime.now();
    }
}