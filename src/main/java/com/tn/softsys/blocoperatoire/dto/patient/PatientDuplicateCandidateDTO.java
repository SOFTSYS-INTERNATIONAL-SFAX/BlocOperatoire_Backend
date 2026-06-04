package com.tn.softsys.blocoperatoire.dto.patient;

import lombok.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDuplicateCandidateDTO {

    private UUID patientId;
    private String mrn;
    private String identiteFHIR;
    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private PatientDuplicateRisk duplicateRisk;
    private List<String> duplicateReasons;
}
