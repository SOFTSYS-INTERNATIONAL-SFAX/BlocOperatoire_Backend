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
}