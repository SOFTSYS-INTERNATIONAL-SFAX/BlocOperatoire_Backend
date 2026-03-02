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
@Table(name = "consentements")
public class Consentement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID consentId;

    private String type;

    private LocalDateTime date;

    private Boolean valide;

    // N → 1 Patient
    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // N → 1 Intervention
    @ManyToOne(optional = false)
    @JoinColumn(name = "intervention_id", nullable = false)
    private Intervention intervention;
}
