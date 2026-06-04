package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.TraitementSSPI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TraitementSSPIRepository extends JpaRepository<TraitementSSPI, UUID> {

    Page<TraitementSSPI> findBySspiSspiIdOrderByHeureAdministrationDesc(UUID sspiId, Pageable pageable);
}
