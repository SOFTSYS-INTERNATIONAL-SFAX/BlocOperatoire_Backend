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
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name = "scores")
public abstract class Score {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID scoreId;

    @Column(nullable = false)
    private Integer valeur;

    @Column(nullable = false)
    private LocalDateTime dateCalcul;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScoreEtat etat;

    // N → 1 Intervention
    @ManyToOne(optional = false)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @PrePersist
    public void prePersist() {
        dateCalcul = LocalDateTime.now();
    }

    // Méthode métier optionnelle
    public void calculerEtat() {
        if (valeur == null) return;

        if (valeur <= 2) {
            etat = ScoreEtat.BAS;
        } else if (valeur <= 4) {
            etat = ScoreEtat.NORMAL;
        } else if (valeur <= 6) {
            etat = ScoreEtat.MOYEN;
        } else {
            etat = ScoreEtat.HAUT;
        }
    }

}

