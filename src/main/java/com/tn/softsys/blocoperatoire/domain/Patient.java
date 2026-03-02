package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

import java.time.LocalDate;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients",
        indexes = {
                @Index(name = "idx_patient_fhir", columnList = "identiteFHIR")
        })
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID patientId;

    @Column(nullable = false, unique = true)
    private String identiteFHIR;

    private String nom;
    private String prenom;

    private LocalDate dateNaissance;

    private String sexe;
    private String nationalite;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Intervention> interventions = new ArrayList<>();

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL)
    private List<Consentement> consentements = new ArrayList<>();

    @OneToMany(mappedBy = "patient")
    private List<FHIRResource> fhirResources = new ArrayList<>();

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}