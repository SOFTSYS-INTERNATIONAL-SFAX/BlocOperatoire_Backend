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
@Table(name = "salles")
public class Salle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID salleId;

    private String nom;
    private String type;
    private Boolean disponible;

    // 1 → N Intervention
    @OneToMany(mappedBy = "salle")
    private List<Intervention> interventions = new ArrayList<>();
}
