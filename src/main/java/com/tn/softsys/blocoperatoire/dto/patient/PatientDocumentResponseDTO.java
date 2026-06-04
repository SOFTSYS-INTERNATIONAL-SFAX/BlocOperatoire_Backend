package com.tn.softsys.blocoperatoire.dto.patient;

import com.tn.softsys.blocoperatoire.domain.PatientDocumentCategory;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientDocumentResponseDTO {

    private UUID documentId;
    private UUID patientId;
    private String title;
    private PatientDocumentCategory category;
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private UUID uploadedByUserId;
    private String uploadedBy;
}
