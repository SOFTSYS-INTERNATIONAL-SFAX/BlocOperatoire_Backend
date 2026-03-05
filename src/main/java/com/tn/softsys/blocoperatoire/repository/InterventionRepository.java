package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface InterventionRepository extends
        JpaRepository<Intervention, UUID>,
        JpaSpecificationExecutor<Intervention> {

    /* ================= RECHERCHE SIMPLE ================= */

    List<Intervention> findByPatient_PatientId(UUID patientId);

    List<Intervention> findByDateIntervention(LocalDate date);

    List<Intervention> findByStatut(StatutIntervention statut);

    /* ================= PLANIFICATION ================= */

    List<Intervention> findBySalle_SalleIdAndDateIntervention(
            UUID salleId,
            LocalDate dateIntervention
    );

    /* ================= DASHBOARD ================= */

    long countByStatut(StatutIntervention statut);

    long countByDateIntervention(LocalDate date);

}