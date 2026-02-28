package com.tn.softsys.blocoperatoire.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(
        name = "fhir_resources",
        indexes = {
                @Index(name = "idx_fhir_patient", columnList = "patient_id"),
                @Index(name = "idx_fhir_intervention", columnList = "intervention_id")
        }
)
public class FHIRResource {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID resourceId;

    @Column(nullable = false)
    private String resourceType;

    @Column(name = "contenu_json", columnDefinition = "TEXT", nullable = false)
    private String payloadJson;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @PrePersist
    public void prePersist() {
        dateCreation = LocalDateTime.now();
    }

    @ManyToOne
    @JoinColumn(name = "patient_id")
    @JsonIgnore
    private Patient patient;

    @ManyToOne
    @JoinColumn(name = "intervention_id")
    @JsonIgnore
    private Intervention intervention;
}