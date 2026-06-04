package com.tn.softsys.blocoperatoire.dto.compterendu;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CompteRenduTemplateResponseDTO {
    private UUID templateId;
    private String name;
    private String libelle;
    private String blocLabel;
    private String content;
    private UUID createdByUserId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
