package com.tn.softsys.blocoperatoire.dto.salle;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SalleRequestDTO {

    @NotBlank
    private String nom;

    @NotBlank
    private String etageBatiment;

    private String equipements;

    @NotNull
    private Boolean active;
}