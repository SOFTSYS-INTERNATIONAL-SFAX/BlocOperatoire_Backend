package com.tn.softsys.blocoperatoire.dto.morgue;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MorgueRequestDTO {

    @NotBlank
    private String nom;

    @NotBlank
    private String localisation;
}