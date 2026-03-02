package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Deces;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface DecesRepository extends JpaRepository<Deces, UUID> {

    Page<Deces> findByDateDecesBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);

    boolean existsByInterventionInterventionId(UUID interventionId);
}