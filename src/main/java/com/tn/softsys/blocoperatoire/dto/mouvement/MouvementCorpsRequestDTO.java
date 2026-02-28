package com.tn.softsys.blocoperatoire.dto.mouvement;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MouvementCorpsRequestDTO {

    @NotNull
    private UUID caseId;

    @NotNull
    private LocalDateTime dateMouvement;

    @NotBlank
    private String typeMouvement; // TRANSFERT, AUTOPSIE, SORTIE
}