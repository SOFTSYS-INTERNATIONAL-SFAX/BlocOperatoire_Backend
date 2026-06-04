package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.MorgueDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MorgueDocumentRepository extends JpaRepository<MorgueDocument, UUID> {

    List<MorgueDocument> findByAutopsieAutopsieIdOrderByUploadedAtDesc(UUID autopsieId);
}
