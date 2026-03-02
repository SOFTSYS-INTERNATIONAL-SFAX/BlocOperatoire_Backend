package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Salle;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SalleRepository extends JpaRepository<Salle, UUID> {

    Page<Salle> findByDisponible(Boolean disponible, Pageable pageable);

    Page<Salle> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Salle> findByTypeContainingIgnoreCase(String type, Pageable pageable);

    Page<Salle> findByNomContainingIgnoreCaseAndTypeContainingIgnoreCase(
            String nom,
            String type,
            Pageable pageable
    );
}