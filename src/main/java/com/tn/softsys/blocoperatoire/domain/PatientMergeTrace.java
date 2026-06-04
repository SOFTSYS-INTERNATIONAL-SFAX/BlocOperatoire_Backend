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
@Table(name = "patient_merge_traces", indexes = {
        @Index(name = "idx_patient_merge_target", columnList = "target_patient_id"),
        @Index(name = "idx_patient_merge_source", columnList = "source_patient_id"),
        @Index(name = "idx_patient_merge_merged_at", columnList = "mergedAt")
})
public class PatientMergeTrace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mergeTraceId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "target_patient_id", nullable = false)
    private Patient targetPatient;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "source_patient_id", nullable = false)
    private Patient sourcePatient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merged_by_user_id")
    private User mergedBy;

    @Column(nullable = false, length = 500)
    private String reason;

    @Column(length = 180)
    private String mergedByDisplayName;

    @Column(nullable = false, updatable = false)
    private LocalDateTime mergedAt;

    @PrePersist
    public void prePersist() {
        mergedAt = LocalDateTime.now();
    }
}
