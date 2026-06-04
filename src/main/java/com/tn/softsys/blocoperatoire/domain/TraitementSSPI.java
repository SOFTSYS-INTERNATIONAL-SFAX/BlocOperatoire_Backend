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
@Table(name = "traitement_sspi")
public class TraitementSSPI {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID traitementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sspi_id", nullable = false)
    private SSPI sspi;

    @Column(nullable = false, length = 150)
    private String nom;

    @Column(length = 120)
    private String dose;

    @Column(length = 80)
    private String voieAdministration;

    @Column(nullable = false)
    private LocalDateTime heureAdministration;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "administre_par_user_id")
    private User administrePar;
}
