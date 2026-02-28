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
@Table(name = "interventions")
public class Intervention {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID interventionId;

    private Boolean urgenceOMS;
    private String statut;
    private Integer dureePrevue;
    private Integer dureeReelle;
    private String codeActe;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "salle_id")
    private Salle salle;

    @ManyToOne
    @JoinColumn(name = "planning_id")
    private PlanningBloc planningBloc;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL, orphanRemoval = true)
    private TempsOperatoire tempsOperatoire;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL)
    private SSPI sspi;

    @OneToOne(mappedBy = "intervention", cascade = CascadeType.ALL)
    private Deces deces;

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL)
    private List<Consentement> consentements = new ArrayList<>();

    @OneToMany(mappedBy = "intervention")
    private List<FHIRResource> fhirResources = new ArrayList<>();

    @OneToMany(mappedBy = "intervention", cascade = CascadeType.ALL)
    private List<Score> scores = new ArrayList<>();
}

