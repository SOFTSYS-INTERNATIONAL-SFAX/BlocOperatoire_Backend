package com.tn.softsys.blocoperatoire.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatientResponseDTO {

    private UUID patientId;

    private String identiteFHIR;
    private String mrn;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Sexe sexe;
    private String nationalite;

    private GroupeSanguin groupeSanguin;
    private String allergies;
    private String traitementsEnCours;
    private String antecedentsMedicaux;

    /* ================= ANTHROPOMÉTRIE ================= */

    private Double tailleCm;
    private Double poidsKg;
    private Double imc;   // Calculé

    private String contactUrgenceNom;
    private String contactUrgenceTelephone;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}