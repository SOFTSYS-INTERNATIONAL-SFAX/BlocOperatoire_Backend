package com.tn.softsys.blocoperatoire.dto.incident;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class IncidentResponseDTO {

    private UUID incidentId;
    private UUID sspiId;
    private String type;
    private String gravite;
    private String description;
    private String action;
    private LocalDateTime declaredAt;
    private UUID declaredByUserId;
    private String declaredByName;
    private Boolean resolu;
    private LocalDateTime dateResolution;
    private UUID resolvedByUserId;
    private String resolvedByName;
}
