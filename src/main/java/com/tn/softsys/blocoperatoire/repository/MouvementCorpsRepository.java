package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface MouvementCorpsRepository
        extends JpaRepository<MouvementCorps, UUID> {

    Page<MouvementCorps> findByCaseMortuaireCaseId(UUID caseId, Pageable pageable);

    Page<MouvementCorps> findByDateMouvementBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    Page<MouvementCorps> findByTypeMouvement(String typeMouvement, Pageable pageable);
}