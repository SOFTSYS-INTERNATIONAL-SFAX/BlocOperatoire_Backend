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
@Table(name = "temps_operatoires")
public class TempsOperatoire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID tempsId;

    private LocalDateTime entreeBloc;
    private LocalDateTime debutAnesthesie;
    private LocalDateTime incision;
    private LocalDateTime finActe;
    private LocalDateTime sortieSalle;

    // 1 → 1 Intervention (côté propriétaire)
    @OneToOne
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;
}
