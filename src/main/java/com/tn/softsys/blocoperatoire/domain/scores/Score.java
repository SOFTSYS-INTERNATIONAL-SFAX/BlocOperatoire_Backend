package com.tn.softsys.blocoperatoire.domain.scores;

import com.tn.softsys.blocoperatoire.domain.Intervention;
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
                @Index(name = "idx_score_intervention", columnList = "intervention_id"),
                @Index(name = "idx_score_date", columnList = "date_calcul"),
                @Index(name = "idx_score_type", columnList = "score_type"),
                @Index(name = "idx_score_calculated_by", columnList = "calculated_by")
        }
)
public abstract class Score {

    /* ===================== ID ===================== */

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "score_id", updatable = false, nullable = false)
    private UUID scoreId;

    /* ===================== VALEUR ===================== */

    @Column(nullable = false)
    protected Integer valeur;

    /* ===================== DATE CALCUL ===================== */

    @Column(name = "date_calcul", nullable = false, updatable = false)
    protected LocalDateTime dateCalcul;

    /* ===================== VERSION ALGORITHME ===================== */

    @Column(nullable = false, length = 150)
    protected String algorithmVersion;

    /* ===================== TYPE SCORE ===================== */

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    protected ScoreType scoreType;

    /* ===================== INTERVENTION ===================== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    /* ===================== IDENTITÉ CALCULATEUR ===================== */

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "calculated_by", nullable = false)
    private User calculatedBy;

    /* ===================== JUSTIFICATION CLINIQUE ===================== */

    @Column(columnDefinition = "TEXT")
    private String justification;

    /* ===================== LIFECYCLE ===================== */

    @PrePersist
    protected void onCreate() {
        this.dateCalcul = LocalDateTime.now();
    }

    /**
     * Les scores sont immuables.
     * Toute tentative de modification doit échouer.
     */
    @PreUpdate
    protected void preventUpdate() {
        throw new UnsupportedOperationException(
                "Scores are immutable and cannot be modified after creation"
        );
    }

    /**
     * Sécurité supplémentaire : empêcher suppression accidentelle.
     */
    @PreRemove
    protected void preventDelete() {
        throw new UnsupportedOperationException(
                "Scores cannot be deleted once created"
        );
    }
}