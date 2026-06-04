package com.tn.softsys.blocoperatoire.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients",
        indexes = {
                @Index(name = "idx_patient_fhir", columnList = "identiteFHIR"),
                @Index(name = "idx_patient_mrn", columnList = "mrn")
        })
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID patientId;

    /* ================= IDENTIFICATION ================= */

    @Column(nullable = false, unique = true)
    private String identiteFHIR;

    @Column(nullable = false, unique = true)
    private String mrn;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;

    private String nationalite;

    /* ================= ANTHROPOMÉTRIE ================= */

    @Positive(message = "La taille doit être positive")
    private Double tailleCm;

    @Positive(message = "Le poids doit être positif")
    private Double poidsKg;

    /* ================= DONNÉES MÉDICALES ================= */

    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "patient_allergies",
            joinColumns = @JoinColumn(name = "patient_id")
    )
    @Column(name = "allergy")
    private List<String> allergies = new ArrayList<>();

    @Column(length = 1000)
    private String traitementsEnCours;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "patient_traitements_habituels",
            joinColumns = @JoinColumn(name = "patient_id")
    )
    @Column(name = "traitement", length = 255)
    private List<String> traitementsHabituels = new ArrayList<>();

    @Column(length = 2000)
    private String antecedentsMedicaux;

    @Builder.Default
    @ElementCollection
    @CollectionTable(
            name = "patient_antecedents_importants",
            joinColumns = @JoinColumn(name = "patient_id")
    )
    @Column(name = "antecedent", length = 255)
    private List<String> antecedentsImportants = new ArrayList<>();

    /* ================= CONTACT URGENCE ================= */

    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String contactUrgenceRelation;

    @Column(length = 1000)
    private String contactUrgenceNotes;

    /* ================= RELATIONS ================= */

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Intervention> interventions = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Consentement> consentements = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient")
    @JsonIgnore
    private List<FHIRResource> fhirResources = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PatientDocument> documents = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PatientClinicalHistoryEntry> clinicalHistoryEntries = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "targetPatient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PatientMergeTrace> mergeHistoryAsTarget = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "sourcePatient")
    @JsonIgnore
    private List<PatientMergeTrace> mergeHistoryAsSource = new ArrayList<>();

    /* ================= AUDIT ================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    /* 🔥 AJOUT CRUCIAL (fix ton erreur) */
    @Column(name = "updated_by_display_name")
    private String updatedByDisplayName;

    /* ================= ARCHIVAGE ================= */

    @Column(nullable = false)
    @Builder.Default
    private Boolean archived = false;

    private LocalDateTime archivedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "archived_by_user_id")
    private User archivedBy;

    private String archivedByDisplayName;

    @Column(length = 1000)
    private String archiveReason;

    /* ================= LIFECYCLE ================= */

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /* ================= CALCUL ================= */

    @Transient
    public Double getImc() {
        if (tailleCm == null || poidsKg == null) return null;
        double tailleMetre = tailleCm / 100.0;
        return poidsKg / (tailleMetre * tailleMetre);
    }
}