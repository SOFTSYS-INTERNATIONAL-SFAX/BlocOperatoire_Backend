package com.tn.softsys.blocoperatoire.domain.scores;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.User;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Inheritance(strategy = InheritanceType.JOINED)
@Table(
        name = "scores",
        indexes = {
                @Index(name = "idx_score_patient", columnList = "patient_id"),
                @Index(name = "idx_score_intervention", columnList = "intervention_id"),
                @Index(name = "idx_score_date", columnList = "date_calcul"),
                @Index(name = "idx_score_type", columnList = "score_type"),
                @Index(name = "idx_score_calculated_by", columnList = "calculated_by")
        }
)
public abstract class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "score_id", updatable = false, nullable = false)
    private UUID scoreId;

    @Column(nullable = false)
    protected Integer valeur;

    @Column(name = "date_calcul", nullable = false, updatable = false)
    protected LocalDateTime dateCalcul;

    @Column(nullable = false, length = 150)
    protected String algorithmVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    protected ScoreType scoreType;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calculated_by", nullable = false)
    private User calculatedBy;

    @Column(columnDefinition = "TEXT")
    private String justification;

    @PrePersist
    protected void onCreate() {
        this.dateCalcul = LocalDateTime.now();
    }

    @PreUpdate
    protected void preventUpdate() {
        throw new UnsupportedOperationException("Scores are immutable and cannot be modified after creation");
    }

    @PreRemove
    protected void preventDelete() {
        throw new UnsupportedOperationException("Scores cannot be deleted once created");
    }
}
