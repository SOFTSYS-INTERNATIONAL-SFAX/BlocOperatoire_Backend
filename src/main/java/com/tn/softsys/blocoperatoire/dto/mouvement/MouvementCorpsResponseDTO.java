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
    private String numeroCase;

    private UUID morgueId;
    private String morgueNom;

    private UUID decesId;

    private UUID interventionId;
    private UUID patientId;
    private String patientNomComplet;
    private String patientMrn;

    private LocalDateTime dateMouvement;
    private String typeMouvement;
}
