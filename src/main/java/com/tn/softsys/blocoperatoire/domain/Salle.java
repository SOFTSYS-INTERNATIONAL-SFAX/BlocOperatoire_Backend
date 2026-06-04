package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    @Column(nullable = false)
    private String nom;

    @Column(name = "nom_en")
    private String nomEn;

    @Column(name = "nom_ar")
    private String nomAr;

    @Column(name = "id_bloc", nullable = false, length = 100)
    private String idBloc;

    @Column(name = "id_bloc_en", length = 100)
    private String idBlocEn;

    @Column(name = "id_bloc_ar", length = 100)
    private String idBlocAr;

    @Column(nullable = false)
    private String etageBatiment;

    @Column(name = "etage_batiment_en")
    private String etageBatimentEn;

    @Column(name = "etage_batiment_ar")
    private String etageBatimentAr;

    @Column(length = 2000)
    private String equipements;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = Boolean.TRUE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private StatutSalle statut = StatutSalle.DISPONIBLE;

    @Builder.Default
    @OneToMany(mappedBy = "salle")
    private List<Intervention> interventions = new ArrayList<>();
}
