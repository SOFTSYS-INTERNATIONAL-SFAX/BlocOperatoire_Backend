package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.CompteRenduDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CompteRenduDocumentRepository extends JpaRepository<CompteRenduDocument, UUID> {
    List<CompteRenduDocument> findByPatientPatientIdOrderByUpdatedAtDesc(UUID patientId);
    List<CompteRenduDocument> findByInterventionInterventionIdOrderByUpdatedAtDesc(UUID interventionId);
}
