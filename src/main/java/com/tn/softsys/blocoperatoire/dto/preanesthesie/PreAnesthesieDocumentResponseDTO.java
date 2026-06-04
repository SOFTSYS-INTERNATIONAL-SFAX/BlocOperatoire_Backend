package com.tn.softsys.blocoperatoire.dto.preanesthesie;

import com.tn.softsys.blocoperatoire.domain.PreAnesthesieDocumentCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreAnesthesieDocumentResponseDTO {

    private UUID documentId;
    private UUID consultationId;
    private UUID patientId;
    private String title;
    private PreAnesthesieDocumentCategory category;
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private UUID uploadedByUserId;
    private String uploadedBy;
}
