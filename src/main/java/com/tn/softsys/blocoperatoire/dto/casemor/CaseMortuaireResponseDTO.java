package com.tn.softsys.blocoperatoire.dto.casemor;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class CaseMortuaireResponseDTO {

    private UUID caseId;
    private String numeroCase;
    private Boolean occupee;
    private UUID morgueId;
    private UUID decesId;
}