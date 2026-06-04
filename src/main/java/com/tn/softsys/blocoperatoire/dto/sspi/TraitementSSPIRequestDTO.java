package com.tn.softsys.blocoperatoire.dto.sspi;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TraitementSSPIRequestDTO {

    @NotBlank
    private String nom;

    private String dose;
    private String voieAdministration;
    private LocalDateTime heureAdministration;
    private String observations;
}
