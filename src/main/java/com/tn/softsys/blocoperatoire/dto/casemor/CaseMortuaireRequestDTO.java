package com.tn.softsys.blocoperatoire.dto.casemor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CaseMortuaireRequestDTO {

    @NotBlank
    private String numeroCase;

    @NotNull
    private UUID morgueId;
}