package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.MorgueDocument;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueDocumentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MorgueDocumentMapper {

    public MorgueDocumentResponseDTO toDTO(MorgueDocument entity) {
        return MorgueDocumentResponseDTO.builder()
                .documentId(entity.getDocumentId())
                .autopsieId(entity.getAutopsie().getAutopsieId())
                .originalFileName(entity.getOriginalFileName())
                .mimeType(entity.getMimeType())
                .sizeBytes(entity.getSizeBytes())
                .uploadedAt(entity.getUploadedAt())
                .uploadedByUserId(entity.getUploadedBy() != null ? entity.getUploadedBy().getUserId() : null)
                .uploadedByEmail(entity.getUploadedBy() != null ? entity.getUploadedBy().getEmail() : null)
                .build();
    }
}
