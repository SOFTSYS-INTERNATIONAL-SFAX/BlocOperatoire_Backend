package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface InterventionRepository extends
        JpaRepository<Intervention, UUID>,
        JpaSpecificationExecutor<Intervention> {

    List<Intervention> findByPatient_PatientId(UUID patientId);

    @EntityGraph(attributePaths = {"patient", "salle", "tempsOperatoire", "sspi"})
    List<Intervention> findByDateIntervention(LocalDate date);

    List<Intervention> findByStatut(StatutIntervention statut);

    List<Intervention> findBySalle_SalleIdAndDateIntervention(
            UUID salleId,
            LocalDate dateIntervention
    );

    long countByStatut(StatutIntervention statut);

    long countByDateIntervention(LocalDate date);

    java.util.Optional<Intervention> findTopByOrderByDateInterventionDesc();

}
