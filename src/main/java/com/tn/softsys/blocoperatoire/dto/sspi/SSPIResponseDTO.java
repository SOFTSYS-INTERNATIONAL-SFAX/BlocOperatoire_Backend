package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class SSPIResponseDTO {

    private UUID sspiId;
    private UUID interventionId;
    private LocalDateTime heureEntree;
    private LocalDateTime heureSortie;
    private String posteCode;
    private String destinationSortie;
    private String motifSortie;
    private Integer aldreteSortie;
    private String decisionMedicale;
    private String observationsSortie;
    private String transmissionResume;
    private UUID sortieValideeParUserId;
    private String sortieValideeParName;
    private Integer surveillanceCount;
    private Integer incidentCount;
}
