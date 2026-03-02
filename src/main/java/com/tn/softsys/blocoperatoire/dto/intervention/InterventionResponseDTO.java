package com.tn.softsys.blocoperatoire.dto.intervention;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class InterventionResponseDTO {

    private UUID interventionId;

    private Boolean urgenceOMS;
    private String statut;
    private Integer dureePrevue;
    private Integer dureeReelle;
    private String codeActe;

    private UUID patientId;
    private UUID salleId;
    private UUID planningId;
}