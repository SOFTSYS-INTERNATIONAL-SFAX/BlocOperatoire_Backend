package com.tn.softsys.blocoperatoire.dto.patient;

import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PatientRequestDTO {

    @NotBlank
    @Size(max = 150)
    private String identiteFHIR;

    @NotBlank
    @Size(max = 100)
    private String mrn;

    @NotBlank
    @Size(max = 100)
    private String nom;

    @NotBlank
    @Size(max = 100)
    private String prenom;

    @NotNull
    @Past
    private LocalDate dateNaissance;

    @NotNull
    private Sexe sexe;

    @Size(max = 100)
    private String nationalite;

    private GroupeSanguin groupeSanguin;

    @Size(max = 1000)
    private String allergies;

    @Size(max = 1000)
    private String traitementsEnCours;

    @Size(max = 2000)
    private String antecedentsMedicaux;

    /* ================= ANTHROPOMÉTRIE ================= */

    @Positive(message = "La taille doit être positive")
    private Double tailleCm;

    @Positive(message = "Le poids doit être positif")
    private Double poidsKg;

    /* ================= CONTACT ================= */

    @Size(max = 255)
    private String contactUrgenceNom;

    @Pattern(
            regexp = "^[0-9+ ]{8,15}$",
            message = "Numéro de téléphone invalide"
    )
    private String contactUrgenceTelephone;
}