package com.tn.softsys.blocoperatoire.domain;

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
    /* ================= ANTHROPOMÉTRIE ================= */

    @Positive(message = "La taille doit être positive")
    private Double tailleCm;

    @Positive(message = "Le poids doit être positif")
    private Double poidsKg;

    @Column(nullable = false, unique = true)
    private String identiteFHIR;

    @Column(nullable = false, unique = true)
    private String mrn; // Numéro dossier (MRN)

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sexe sexe;
    private String nationalite;

    /* ================= DONNÉES MÉDICALES ================= */


    @Enumerated(EnumType.STRING)
    private GroupeSanguin groupeSanguin;

    @Column(length = 1000)
    private String allergies;

    @Column(length = 1000)
    private String traitementsEnCours;

    @Column(length = 2000)
    private String antecedentsMedicaux;

    /* ================= CONTACT URGENCE ================= */

    private String contactUrgenceNom;

    private String contactUrgenceTelephone;

    /* ================= RELATIONS ================= */

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Intervention> interventions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Consentement> consentements = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<FHIRResource> fhirResources = new ArrayList<>();

    /* ================= AUDIT ================= */

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
    @Transient
    public Double getImc() {
        if (tailleCm == null || poidsKg == null) return null;
        double tailleMetre = tailleCm / 100.0;
        return poidsKg / (tailleMetre * tailleMetre);
    }
    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}