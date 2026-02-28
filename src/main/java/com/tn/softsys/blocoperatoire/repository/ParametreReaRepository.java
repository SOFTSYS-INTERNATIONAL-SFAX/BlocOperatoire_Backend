package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.ParametreRea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public interface ParametreReaRepository extends JpaRepository<ParametreRea, UUID> {

    // Toutes les mesures d'une réanimation
    Page<ParametreRea> findByReanimationReaId(
            UUID reaId,
            Pageable pageable
    );

    // Recherche par ventilation
    Page<ParametreRea> findByVentilation(
            Boolean ventilation,
            Pageable pageable
    );

    // Recherche par vasopresseur
    Page<ParametreRea> findByVasopresseurActif(
            Boolean vasopresseurActif,
            Pageable pageable
    );

    // Recherche par période de mesure
    Page<ParametreRea> findByDateMesureBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable
    );

    // Historique trié automatiquement
    Page<ParametreRea> findByReanimationReaIdOrderByDateMesureAsc(
            UUID reaId,
            Pageable pageable
    );
}