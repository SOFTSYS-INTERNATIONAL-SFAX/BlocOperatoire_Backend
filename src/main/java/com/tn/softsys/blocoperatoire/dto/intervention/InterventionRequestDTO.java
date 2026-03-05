package com.tn.softsys.blocoperatoire.dto.intervention;

import com.tn.softsys.blocoperatoire.domain.*;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class InterventionRequestDTO {

    /* ================= RELATIONS ================= */

    @NotNull(message = "Patient obligatoire")
    private UUID patientId;

    private UUID salleId;
    private UUID planningId;

    private UUID chirurgienId;
    private UUID anesthesisteId;

    /* ================= DONNÉES MÉDICALES ================= */

    @NotBlank(message = "Nom intervention obligatoire")
    private String nomIntervention;

    private String codeActe;

    private Lateralite lateralite;

    @NotNull(message = "Statut obligatoire")
    private StatutIntervention statut;

    private PrioriteIntervention priorite;

    private Boolean urgenceOMS;

    @Positive(message = "Durée prévue doit être positive")
    private Integer dureePrevue;

    private Integer dureeReelle;

    @Size(max = 2000)
    private String diagnostic;

    /* ================= PLANIFICATION ================= */

    @NotNull(message = "Date intervention obligatoire")
    private LocalDate dateIntervention;

    private LocalTime heureDebut;
}