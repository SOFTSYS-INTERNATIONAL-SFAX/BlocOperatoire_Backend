package com.tn.softsys.blocoperatoire.dto.incident;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class IncidentCreateRequestDTO {

    @NotBlank
    private String type;

    @NotBlank
    private String gravite;

    private String description;
    private String action;
}
