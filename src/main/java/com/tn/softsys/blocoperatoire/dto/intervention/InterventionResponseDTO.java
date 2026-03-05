package com.tn.softsys.blocoperatoire.dto.intervention;

import com.tn.softsys.blocoperatoire.domain.*;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
public class InterventionResponseDTO {

    private UUID interventionId;

    /* ================= DONNÉES MÉDICALES ================= */

    private String nomIntervention;
    private String codeActe;

    private Lateralite lateralite;
    private StatutIntervention statut;
    private PrioriteIntervention priorite;

    private Boolean urgenceOMS;

    private Integer dureePrevue;
    private Integer dureeReelle;

    private String diagnostic;

    /* ================= PLANIFICATION ================= */

    private LocalDate dateIntervention;
    private LocalTime heureDebut;

    /* ================= RELATIONS ================= */

    private UUID patientId;
    private UUID salleId;
    private UUID planningId;

    private UUID chirurgienId;
    private UUID anesthesisteId;

    /* ================= EXTENSIONS ================= */

    private Boolean hasTempsOperatoire;
    private Boolean hasSSPI;
    private Boolean hasDeces;
}