package com.tn.softsys.blocoperatoire.domain;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "interventions",
        indexes = {
                @Index(name = "idx_intervention_patient", columnList = "patient_id"),
                @Index(name = "idx_intervention_date", columnList = "dateIntervention"),
                @Index(name = "idx_intervention_statut", columnList = "statut")
        })
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID interventionId;

    /* ================= INFORMATIONS MÉDICALES ================= */

    @Column(nullable = false)
    private String nomIntervention;

    private String codeActe;

    @Enumerated(EnumType.STRING)
    private Lateralite lateralite;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutIntervention statut;

    @Enumerated(EnumType.STRING)
    private PrioriteIntervention priorite;

    private Boolean urgenceOMS;

    private Integer dureePrevue;
    private Integer dureeReelle;

    @Column(length = 2000)
    private String diagnostic;

    /* ================= PLANIFICATION ================= */

    @Column(nullable = false)
    private LocalDate dateIntervention;

    private LocalTime heureDebut;

    /* ================= RELATIONS PRINCIPALES ================= */

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "planning_id")
    private PlanningBloc planningBloc;

    /* ================= MÉDECINS ================= */

    @ManyToOne
    @JoinColumn(name = "chirurgien_id")
    private User chirurgien;

    @ManyToOne
    @JoinColumn(name = "anesthesiste_id")
    private User anesthesiste;

    /* ================= EXTENSIONS MÉDICALES ================= */

    @OneToOne(mappedBy = "intervention",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private TempsOperatoire tempsOperatoire;

    @OneToOne(mappedBy = "intervention",
            cascade = CascadeType.ALL)
    private SSPI sspi;

    @OneToOne(mappedBy = "intervention",
            cascade = CascadeType.ALL)
    private Deces deces;

    @OneToMany(mappedBy = "intervention",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Consentement> consentements = new ArrayList<>();

    @OneToMany(mappedBy = "intervention")
    private List<FHIRResource> fhirResources = new ArrayList<>();

    @OneToMany(mappedBy = "intervention",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Score> scores = new ArrayList<>();

}