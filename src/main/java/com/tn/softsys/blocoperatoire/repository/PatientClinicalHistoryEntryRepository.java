package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.PatientClinicalHistoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientClinicalHistoryEntryRepository extends JpaRepository<PatientClinicalHistoryEntry, UUID> {

    List<PatientClinicalHistoryEntry> findByPatientPatientIdOrderByEventDateDescCreatedAtDesc(UUID patientId);

    long countByPatientPatientId(UUID patientId);
}
