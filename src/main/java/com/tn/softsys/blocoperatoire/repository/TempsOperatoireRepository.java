package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.TempsOperatoire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface TempsOperatoireRepository
        extends JpaRepository<TempsOperatoire, UUID> {

    boolean existsByInterventionInterventionId(UUID interventionId);

    Optional<TempsOperatoire> findByInterventionInterventionId(UUID interventionId);

    Page<TempsOperatoire> findByInterventionInterventionId(
            UUID interventionId,
            Pageable pageable);

    Page<TempsOperatoire> findByEntreeBlocBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
}