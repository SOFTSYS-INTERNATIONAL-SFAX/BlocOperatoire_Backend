package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    Page<Score> findByIntervention_InterventionId(UUID interventionId, Pageable pageable);

    Page<Score> findByIntervention_Patient_PatientId(UUID patientId, Pageable pageable);

    Page<Score> findByScoreType(ScoreType scoreType, Pageable pageable);

}