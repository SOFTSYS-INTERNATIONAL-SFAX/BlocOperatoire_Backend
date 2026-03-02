package com.tn.softsys.blocoperatoire.dto.patient;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRequestDTO {

    @NotBlank(message = "L'identité FHIR est obligatoire")
    @Size(max = 150, message = "L'identité FHIR ne doit pas dépasser 150 caractères")
    private String identiteFHIR;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @NotNull(message = "La date de naissance est obligatoire")
    @Past(message = "La date de naissance doit être dans le passé")
    private LocalDate dateNaissance;

    @NotBlank(message = "Le sexe est obligatoire")
    @Pattern(
            regexp = "HOMME|FEMME|AUTRE",
            message = "Le sexe doit être HOMME, FEMME ou AUTRE"
    )
    private String sexe;

    @Size(max = 100, message = "La nationalité ne doit pas dépasser 100 caractères")
    private String nationalite;
}