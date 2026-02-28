package com.tn.softsys.blocoperatoire.dto.mouvement;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MouvementCorpsResponseDTO {

    private UUID mouvementId;
    private UUID caseId;
    private LocalDateTime dateMouvement;
    private String typeMouvement;
}