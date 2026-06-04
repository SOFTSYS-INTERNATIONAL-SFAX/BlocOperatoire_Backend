package com.tn.softsys.blocoperatoire.dto.patient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    /* 🔴 CORRECTION */
    private List<String> allergies;

    private String traitementsEnCours;
    private List<String> traitementsHabituels;
    private String antecedentsMedicaux;
    private List<String> antecedentsImportants;

    private Double tailleCm;
    private Double poidsKg;
    private Double imc;

    private String contactUrgenceNom;
    private String contactUrgenceTelephone;
    private String contactUrgenceRelation;
    private String contactUrgenceNotes;

    private Boolean archived;
    private PatientArchiveTraceDTO archiveTrace;
    private Long documentCount;
    private Long clinicalHistoryCount;
    private PatientDuplicateRisk duplicateRisk;
    private List<String> duplicateReasons;
    private List<UUID> duplicatePatientIds;
    private List<PatientMergeTraceResponseDTO> mergeHistory;
    private List<PatientChangeLogDTO> changeLogs;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String updatedByDisplayName;
}
