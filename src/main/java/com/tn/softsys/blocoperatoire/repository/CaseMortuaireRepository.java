package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CaseMortuaireRepository
        extends JpaRepository<CaseMortuaire, UUID> {

    Page<CaseMortuaire> findByMorgueMorgueId(UUID morgueId, Pageable pageable);

    Page<CaseMortuaire> findByOccupee(Boolean occupee, Pageable pageable);

    boolean existsByNumeroCase(String numeroCase);
}