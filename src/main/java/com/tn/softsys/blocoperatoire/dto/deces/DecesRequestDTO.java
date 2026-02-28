package com.tn.softsys.blocoperatoire.dto.deces;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class DecesRequestDTO {

    @NotNull
    private UUID interventionId;

    @NotNull
    private LocalDateTime dateDeces;

    @NotBlank
    private String cause;

    @NotBlank
    private String constatPar;
}