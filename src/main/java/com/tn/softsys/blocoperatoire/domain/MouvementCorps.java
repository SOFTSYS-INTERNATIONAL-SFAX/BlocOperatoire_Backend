package com.tn.softsys.blocoperatoire.domain;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "mouvements_corps")
public class MouvementCorps {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID mouvementId;

    private LocalDateTime dateMouvement;
    private String typeMouvement; // TRANSFERT, AUTOPSIE, SORTIE

    @ManyToOne
    @JoinColumn(name = "case_id", nullable = false)
    private CaseMortuaire caseMortuaire;
}
