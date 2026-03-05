package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Salle;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalleRepository extends JpaRepository<Salle, UUID> {

    Page<Salle> findByActive(Boolean active, Pageable pageable);

    Page<Salle> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Salle> findByEtageBatimentContainingIgnoreCase(String etageBatiment, Pageable pageable);

    Page<Salle> findByNomContainingIgnoreCaseAndEtageBatimentContainingIgnoreCase(
            String nom,
            String etageBatiment,
            Pageable pageable
    );
}