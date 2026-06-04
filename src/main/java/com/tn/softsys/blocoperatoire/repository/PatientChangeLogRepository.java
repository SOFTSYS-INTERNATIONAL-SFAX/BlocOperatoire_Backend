package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.PatientChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientChangeLogRepository extends JpaRepository<PatientChangeLog, UUID> {

    List<PatientChangeLog> findByPatientIdOrderByChangedAtDesc(UUID patientId);
}