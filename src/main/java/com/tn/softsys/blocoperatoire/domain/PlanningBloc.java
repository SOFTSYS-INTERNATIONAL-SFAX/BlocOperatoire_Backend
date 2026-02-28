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
@Table(name = "planning_blocs")
public class PlanningBloc {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID planningId;

    private LocalDateTime date;

    // 1 → N Intervention
    @OneToMany(mappedBy = "planningBloc")
    private List<Intervention> interventions = new ArrayList<>();
}
