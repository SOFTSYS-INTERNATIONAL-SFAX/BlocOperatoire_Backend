package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "parametres_rea",
        indexes = {
                @Index(name = "idx_param_rea", columnList = "rea_id"),
                @Index(name = "idx_param_date", columnList = "date_mesure")
        })
public class ParametreRea {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID paramId;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime dateMesure;

    @NotNull
    @Column(nullable = false)
    private Boolean ventilation;

    @NotNull
    @Column(nullable = false)
    private Boolean vasopresseurActif;

    @NotNull
    @Column(nullable = false)
    private Double diurese; // 🔥 Double au lieu de Float

    @ManyToOne(optional = false)
    @JoinColumn(name = "rea_id", nullable = false)
    private Reanimation reanimation;
}