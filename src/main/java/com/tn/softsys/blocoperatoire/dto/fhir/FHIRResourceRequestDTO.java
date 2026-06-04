package com.tn.softsys.blocoperatoire.dto.fhir;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class FHIRResourceRequestDTO {

    private UUID patientId;
    private UUID interventionId;

    @NotBlank
    @Size(max = 120)
    private String resourceType;

    @NotBlank
    private String payloadJson;
}
