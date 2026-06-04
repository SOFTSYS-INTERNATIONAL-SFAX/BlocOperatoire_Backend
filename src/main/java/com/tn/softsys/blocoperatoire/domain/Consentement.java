package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consentements")
public class Consentement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID consentId;

    private String type;

    private LocalDateTime date;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ConsentementStatut statut = ConsentementStatut.BROUILLON;

    @Builder.Default
    private Boolean valide = false;

    private LocalDateTime verifiedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "verified_by_user_id")
    private User verifiedBy;

    private String verifiedByName;

    // N → 1 Patient
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // N → 1 Intervention
    @ManyToOne(optional = false)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;

    @PrePersist
    @PreUpdate
    public void syncVerificationState() {
        ConsentementStatut effectiveStatut = statut;

        if (effectiveStatut == null) {
            effectiveStatut = Boolean.TRUE.equals(valide)
                    ? ConsentementStatut.VERIFIE
                    : ConsentementStatut.BROUILLON;
        }

        statut = effectiveStatut;
        valide = ConsentementStatut.VERIFIE.equals(effectiveStatut);

        if (!Boolean.TRUE.equals(valide)) {
            verifiedAt = null;
            verifiedBy = null;
            verifiedByName = null;
        }
    }
}
