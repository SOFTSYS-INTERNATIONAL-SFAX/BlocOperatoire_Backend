package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Score;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ScoreRepository extends JpaRepository<Score, UUID> {

    Page<Score> findByInterventionInterventionId(UUID interventionId, Pageable pageable);
}