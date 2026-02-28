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
@Table(name = "sspi")
public class SSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sspiId;

    private LocalDateTime heureEntree;
    private LocalDateTime heureSortie;

    // 1 → 1 Intervention
    @OneToOne
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;

    // 1 → N Surveillance
    @OneToMany(mappedBy = "sspi", cascade = CascadeType.ALL)
    private List<SurveillanceSSPI> surveillances = new ArrayList<>();

    // 1 → N Incidents
    @OneToMany(mappedBy = "sspi", cascade = CascadeType.ALL)
    private List<IncidentSSPI> incidents = new ArrayList<>();

    // 0..1 → 1 Reanimation
    @OneToOne(mappedBy = "sspi", cascade = CascadeType.ALL)
    private Reanimation reanimation;
}
