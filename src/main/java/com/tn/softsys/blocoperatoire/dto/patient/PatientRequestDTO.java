package com.tn.softsys.blocoperatoire.dto.patient;

import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientRequestDTO {

    @NotBlank(message = "L'identite FHIR est obligatoire")
    private String identiteFHIR;

    @NotBlank(message = "Le MRN est obligatoire")
    private String mrn;

    @NotBlank(message = "Le nom est obligatoire")
    private String nom;

    @NotBlank(message = "Le prenom est obligatoire")
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    private LocalDate dateNaissance;

    @NotNull(message = "Le sexe est obligatoire")
    private Sexe sexe;

    private String nationalite;

    private GroupeSanguin groupeSanguin;

    @Builder.Default
    private List<String> allergies = new ArrayList<>();

    private String traitementsEnCours;

    @Builder.Default
    private List<String> traitementsHabituels = new ArrayList<>();

    private String antecedentsMedicaux;

    @Builder.Default
    private List<String> antecedentsImportants = new ArrayList<>();

    @NotNull(message = "La taille est obligatoire")
    @Positive(message = "La taille doit etre positive")
    private Double tailleCm;

    @NotNull(message = "Le poids est obligatoire")
    @Positive(message = "Le poids doit etre positif")
    private Double poidsKg;

    @NotBlank(message = "Le contact d'urgence est obligatoire")
    private String contactUrgenceNom;

    @NotBlank(message = "Le telephone d'urgence est obligatoire")
    private String contactUrgenceTelephone;

    private String contactUrgenceRelation;
    private String contactUrgenceNotes;
}
