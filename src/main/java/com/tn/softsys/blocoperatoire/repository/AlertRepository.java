package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Alert;
import com.tn.softsys.blocoperatoire.domain.AlertType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository extends JpaRepository<Alert, UUID> {

    @EntityGraph(attributePaths = {"intervention", "intervention.patient", "salle", "acknowledgedBy"})
    List<Alert> findByActiveTrueOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"intervention", "intervention.patient", "salle", "acknowledgedBy"})
    Page<Alert> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @EntityGraph(attributePaths = {"intervention", "intervention.patient", "salle", "acknowledgedBy"})
    List<Alert> findByViewedFalseOrderByCreatedAtDesc();

    Optional<Alert> findByTypeAndIntervention_InterventionIdAndActiveTrue(
            AlertType type,
            UUID interventionId
    );

    Optional<Alert> findByTypeAndSalle_SalleIdAndActiveTrue(
            AlertType type,
            UUID salleId
    );

    List<Alert> findByIntervention_InterventionIdAndActiveTrue(UUID interventionId);

    List<Alert> findBySalle_SalleIdAndActiveTrue(UUID salleId);

    List<Alert> findAllByTypeAndIntervention_InterventionIdAndActiveTrue(
            AlertType type,
            UUID interventionId
    );
}
