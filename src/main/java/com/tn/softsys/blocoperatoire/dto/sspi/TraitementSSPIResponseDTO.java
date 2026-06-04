package com.tn.softsys.blocoperatoire.dto.sspi;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TraitementSSPIResponseDTO {

    private UUID traitementId;
    private UUID sspiId;
    private String nom;
    private String dose;
    private String voieAdministration;
    private LocalDateTime heureAdministration;
    private String observations;
    private UUID administreParUserId;
    private String administreParName;
}
