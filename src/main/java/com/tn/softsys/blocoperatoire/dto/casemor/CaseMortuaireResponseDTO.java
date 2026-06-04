package com.tn.softsys.blocoperatoire.dto.casemor;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class CaseMortuaireResponseDTO {

    private UUID caseId;
    private String numeroCase;
    private Boolean occupee;

    private UUID morgueId;
    private String morgueNom;

    private UUID decesId;
    private LocalDateTime dateDeces;
    private String causeDeces;

    private UUID interventionId;
    private UUID patientId;
    private String patientNomComplet;
    private String patientMrn;

    private Integer mouvementsCount;
    private String dernierMouvementType;
    private LocalDateTime dernierMouvementDate;
}
