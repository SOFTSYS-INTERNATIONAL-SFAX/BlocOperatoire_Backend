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
@Table(name = "sspi")
public class SSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID sspiId;

    @Column(nullable = false)
    private LocalDateTime heureEntree;

    private LocalDateTime heureSortie;

    @Column(length = 50)
    private String posteCode;

    @Column(length = 100)
    private String destinationSortie;

    @Column(length = 150)
    private String motifSortie;

    private Integer aldreteSortie;

    @Column(length = 150)
    private String decisionMedicale;

    @Column(columnDefinition = "TEXT")
    private String observationsSortie;

    @Column(columnDefinition = "TEXT")
    private String transmissionResume;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sortie_validee_par")
    private User sortieValideePar;

    @OneToOne
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;

    @OneToMany(mappedBy = "sspi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveillanceSSPI> surveillances = new ArrayList<>();

    @OneToMany(mappedBy = "sspi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IncidentSSPI> incidents = new ArrayList<>();

    @OneToMany(mappedBy = "sspi", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TraitementSSPI> traitements = new ArrayList<>();

    @OneToOne(mappedBy = "sspi", cascade = CascadeType.ALL)
    private Reanimation reanimation;
}
