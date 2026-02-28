package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "cases_mortuaires")
public class CaseMortuaire {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID caseId;

    @Column(nullable = false, unique = true)
    private String numeroCase;

    @Column(nullable = false)
    private Boolean occupee;

    // 1 → 1 Décès (OPTIONNEL)
    @OneToOne
    @JoinColumn(name = "deces_id", unique = true) // ❌ enlever nullable = false
    private Deces deces;

    // N → 1 Morgue
    @ManyToOne(optional = false)
    @JoinColumn(name = "morgue_id", nullable = false)
    private Morgue morgue;

    // 1 → N MouvementCorps
    @OneToMany(mappedBy = "caseMortuaire", cascade = CascadeType.ALL)
    private List<MouvementCorps> mouvements = new ArrayList<>();
}