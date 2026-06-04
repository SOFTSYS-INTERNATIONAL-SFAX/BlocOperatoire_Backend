package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "consultations_pre_anesthesiques", indexes = {
        @Index(name = "idx_pre_anesth_patient", columnList = "patient_id"),
        @Index(name = "idx_pre_anesth_intervention", columnList = "intervention_id"),
        @Index(name = "idx_pre_anesth_validated_at", columnList = "validatedAt")
})
public class ConsultationPreAnesthesique {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID consultationId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consentement_id")
    private Consentement consentement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_id")
    private Intervention intervention;

    @Column(name = "asa_id")
    private UUID asaId;

    @Column(name = "asa_code", length = 30)
    private String asaCode;

    @Column(nullable = false)
    private Boolean urgence;

    private Double poidsKg;
    private Double tailleCm;
    private Integer paSystolique;
    private Integer paDiastolique;
    private Integer frequenceCardiaque;
    private Integer frequenceRespiratoire;
    private Integer spo2;
    private Integer temperatureDixieme;

    @Column(length = 30)
    private String mallampatiCode;

    private Integer ouvertureBucaleMm;

    @Column(length = 30)
    private String mobiliteCervicale;

    @Column(length = 60)
    private String typeAnesthesie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anesthesiste_user_id")
    private User anesthesiste;

    @Column(length = 2000)
    private String considerations;

    @Column(length = 2000)
    private String allergiesResume;

    @Column(length = 2000)
    private String traitementsChroniques;

    @Column(length = 2000)
    private String antecedentsMedicauxResume;

    @Column(length = 2000)
    private String antecedentsAnesthesiques;

    @Column(length = 2000)
    private String evaluationCardioRespiratoire;

    @Column(length = 2000)
    private String examensComplementairesResume;

    @Column(length = 500)
    private String etatDentaire;

    @Column(length = 500)
    private String risqueHemorragique;

    @Column(length = 2000)
    private String strategieAnesthesique;

    @Column(nullable = false)
    private Boolean jeuneConfirme;

    private Integer jeuneHeures;

    @Column(nullable = false)
    private Boolean consentementEclaireObtenu;

    private Boolean voieAerienneDifficileSuspectee;

    @Column(length = 2000)
    private String notesComplementaires;

    @Column(nullable = false)
    private Boolean validee;

    private String medecinNom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validated_by_user_id")
    private User validatedBy;

    @Column(length = 180)
    private String validatedByName;

    private LocalDateTime validatedAt;

    @Column(length = 2000)
    private String validationCommentaire;

    private Integer riskScore;

    @Column(length = 40)
    private String riskLevel;

    @Column(length = 500)
    private String riskSummary;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PreAnesthesieDocument> documents = new ArrayList<>();

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        if (urgence == null) urgence = false;
        if (jeuneConfirme == null) jeuneConfirme = false;
        if (consentementEclaireObtenu == null) consentementEclaireObtenu = false;
        if (voieAerienneDifficileSuspectee == null) voieAerienneDifficileSuspectee = false;
        if (validee == null) validee = false;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
