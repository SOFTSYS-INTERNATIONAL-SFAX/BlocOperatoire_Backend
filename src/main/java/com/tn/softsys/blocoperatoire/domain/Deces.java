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
@Table(name = "deces")
public class Deces {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID decesId;

    private LocalDateTime dateDeces;
    private String cause;
    private String constatPar;

    // 0..1 → 1 Intervention
    @OneToOne
    @JoinColumn(name = "intervention_id", nullable = false, unique = true)
    private Intervention intervention;

    // 1 → 1 CaseMortuaire
    @OneToOne(mappedBy = "deces", cascade = CascadeType.ALL)
    private CaseMortuaire caseMortuaire;
}
