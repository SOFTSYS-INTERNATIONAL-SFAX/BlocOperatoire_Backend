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

    /* ================= FORMULAIRE ================= */

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String etageBatiment;

    @Column(length = 2000)
    private String equipements; // Stocké en texte séparé par virgules

    @Column(nullable = false)
    private Boolean active;

    /* ================= RELATION ================= */

    @OneToMany(mappedBy = "salle")
    private List<Intervention> interventions = new ArrayList<>();
}