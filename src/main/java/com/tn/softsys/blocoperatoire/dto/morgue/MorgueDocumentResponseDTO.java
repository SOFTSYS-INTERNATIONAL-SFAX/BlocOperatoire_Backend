package com.tn.softsys.blocoperatoire.dto.morgue;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class MorgueDocumentResponseDTO {

    private UUID documentId;
    private UUID autopsieId;
    private String originalFileName;
    private String mimeType;
    private Long sizeBytes;
    private LocalDateTime uploadedAt;
    private UUID uploadedByUserId;
    private String uploadedByEmail;
}
