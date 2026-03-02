package com.tn.softsys.blocoperatoire.dto.consentement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConsentementRequestDTO {

    @NotNull
    private UUID patientId;

    @NotNull
    private UUID interventionId;

    @NotBlank
    private String type;

    @NotNull
    private LocalDateTime date;

    @NotNull
    private Boolean valide;
}