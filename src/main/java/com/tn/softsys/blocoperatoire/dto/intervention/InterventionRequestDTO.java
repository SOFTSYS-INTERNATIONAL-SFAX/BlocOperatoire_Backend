package com.tn.softsys.blocoperatoire.dto.intervention;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class InterventionRequestDTO {

    @NotNull
    private UUID patientId;

    private UUID salleId;

    private UUID planningId;

    private Boolean urgenceOMS;

    @NotBlank
    private String statut;

    @Positive
    private Integer dureePrevue;

    private Integer dureeReelle;

    @NotBlank
    private String codeActe;
}