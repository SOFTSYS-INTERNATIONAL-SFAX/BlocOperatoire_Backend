package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.ConsentementDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ConsentementDocumentRepository extends JpaRepository<ConsentementDocument, UUID> {

    List<ConsentementDocument> findByConsentementConsentIdOrderByUploadedAtDesc(UUID consentId);

    long countByConsentementConsentId(UUID consentId);
}
