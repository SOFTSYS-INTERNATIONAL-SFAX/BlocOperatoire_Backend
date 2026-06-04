package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.PreAnesthesieDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PreAnesthesieDocumentRepository extends JpaRepository<PreAnesthesieDocument, UUID> {

    List<PreAnesthesieDocument> findByConsultationConsultationIdOrderByUploadedAtDesc(UUID consultationId);

    long countByConsultationConsultationId(UUID consultationId);
}
