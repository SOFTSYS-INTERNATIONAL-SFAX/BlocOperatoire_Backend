package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.sofa.Sofa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SofaRepository extends JpaRepository<Sofa, UUID> {

    Optional<Sofa> findTopByIntervention_InterventionIdAndDateCalculBeforeOrderByDateCalculDesc(
            UUID interventionId,
            LocalDateTime before
    );
}