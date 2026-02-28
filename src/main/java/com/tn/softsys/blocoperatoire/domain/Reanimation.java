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
@Table(name = "reanimations")
public class Reanimation {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reaId;

    private LocalDateTime dateEntree;
    private LocalDateTime dateSortie;

    @OneToOne
    @JoinColumn(name = "sspi_id", nullable = false, unique = true)
    private SSPI sspi;

    @OneToMany(mappedBy = "reanimation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @OrderBy("dateMesure ASC")
    private List<ParametreRea> parametres = new ArrayList<>();
}