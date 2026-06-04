package com.tn.softsys.blocoperatoire.dto.consentement;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsentementDocumentResponseDTO {

    private UUID documentId;
    private UUID consentId;
    private UUID patientId;
    private UUID interventionId;
    private String title;
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private UUID uploadedByUserId;
    private String uploadedBy;
}
