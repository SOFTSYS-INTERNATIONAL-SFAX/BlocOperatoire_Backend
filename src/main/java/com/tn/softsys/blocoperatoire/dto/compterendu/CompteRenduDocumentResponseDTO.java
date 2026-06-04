package com.tn.softsys.blocoperatoire.dto.compterendu;

import com.tn.softsys.blocoperatoire.domain.CompteRenduDocumentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class CompteRenduDocumentResponseDTO {
    private UUID documentId;
    private UUID patientId;
    private String patientLabel;
    private UUID interventionId;
    private String interventionLabel;
    private UUID templateId;
    private String templateName;
    private String blocLabel;
    private String title;
    private String content;
    private CompteRenduDocumentStatus status;
    private UUID authoredByUserId;
    private String authoredBy;
    private UUID dictationTemplateId;
    private String audioOriginalFileName;
    private String audioMimeType;
    private Long audioSizeBytes;
    private Integer dictationDurationSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
