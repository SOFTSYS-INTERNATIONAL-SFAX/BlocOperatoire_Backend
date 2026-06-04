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
@Builder
@Table(name = "surveillance_sspi")
public class SurveillanceSSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID surveillanceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sspi_id", nullable = false)
    private SSPI sspi;

    @Column(nullable = false)
    private LocalDateTime dateMesure;

    private Integer frequenceCardiaque;
    private Integer paSystolique;
    private Integer paDiastolique;
    private Integer frequenceRespiratoire;
    private Integer spo2;
    private Integer temperatureDixieme;
    private Integer douleurEva;
    private Integer scoreConscience;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mesuree_par_user_id")
    private User mesureePar;
}
