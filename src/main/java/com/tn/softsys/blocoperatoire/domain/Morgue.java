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
@Table(name = "morgues")
public class Morgue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID morgueId;

    private String nom;
    private String localisation;

    @OneToMany(mappedBy = "morgue")
    private List<CaseMortuaire> cases = new ArrayList<>();
}
