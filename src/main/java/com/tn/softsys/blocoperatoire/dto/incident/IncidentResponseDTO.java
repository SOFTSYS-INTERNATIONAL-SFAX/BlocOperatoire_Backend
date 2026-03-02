package com.tn.softsys.blocoperatoire.dto.incident;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class IncidentResponseDTO {

    private UUID incidentId;
    private String type;
    private String gravite;
    private String action;
}