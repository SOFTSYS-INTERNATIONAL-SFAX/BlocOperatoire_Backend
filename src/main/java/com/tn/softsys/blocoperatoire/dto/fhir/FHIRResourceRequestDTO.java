package com.tn.softsys.blocoperatoire.dto.fhir;

import lombok.Data;

import java.util.UUID;

@Data
public class FHIRResourceRequestDTO {

    private UUID patientId;
    private UUID interventionId;
    private String resourceType;
    private String payloadJson;
}