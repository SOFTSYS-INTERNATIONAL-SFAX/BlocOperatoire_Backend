package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InterventionRepository extends JpaRepository<Intervention, UUID> {

    Page<Intervention> findByPatientPatientId(UUID patientId, Pageable pageable);

    Page<Intervention> findByStatutContainingIgnoreCase(String statut, Pageable pageable);

    Page<Intervention> findByUrgenceOMSTrue(Pageable pageable);

    Page<Intervention> findByCodeActeContainingIgnoreCase(String codeActe, Pageable pageable);
}