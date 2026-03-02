package com.tn.softsys.blocoperatoire.dto.fhir;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class FHIRResourceResponseDTO {

    private UUID resourceId;
    private String resourceType;
    private String payloadJson;
    private LocalDateTime dateCreation;

    private UUID patientId;
    private UUID interventionId;
}