package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.SSPI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SSPIRepository extends JpaRepository<SSPI, UUID> {

    Optional<SSPI> findByInterventionInterventionId(UUID interventionId);

    boolean existsByInterventionInterventionId(UUID interventionId);

    Page<SSPI> findByHeureEntreeBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    @EntityGraph(attributePaths = {"intervention", "intervention.patient"})
    List<SSPI> findByHeureSortieIsNull();

    @EntityGraph(attributePaths = {"intervention", "intervention.patient"})
    List<SSPI> findByHeureSortieBetween(LocalDateTime from, LocalDateTime to);
}
