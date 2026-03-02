package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientResponseDTO {

    private UUID patientId;

    private String identiteFHIR;

    private String nom;

    private String prenom;

    private LocalDate dateNaissance;

    private String sexe;

    private String nationalite;

    // Traçabilité médico-légale
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}