package com.tn.softsys.blocoperatoire.dto.consentement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class ConsentementResponseDTO {

    private UUID consentId;
    private String type;
    private LocalDateTime date;
    private Boolean valide;

    private UUID patientId;
    private UUID interventionId;
}