package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    Page<Score> findByIntervention_InterventionId(UUID interventionId, Pageable pageable);

    Page<Score> findByPatient_PatientId(UUID patientId, Pageable pageable);

    Page<Score> findByScoreType(ScoreType scoreType, Pageable pageable);

    @EntityGraph(attributePaths = {"intervention", "patient"})
    List<Score> findByScoreTypeAndIntervention_InterventionIdInOrderByDateCalculDesc(
            ScoreType scoreType,
            Collection<UUID> interventionIds
    );
}
