package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.PatientMergeTrace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PatientMergeTraceRepository extends JpaRepository<PatientMergeTrace, UUID> {

    List<PatientMergeTrace> findByTargetPatientPatientIdOrderByMergedAtDesc(UUID patientId);

    List<PatientMergeTrace> findByTargetPatientPatientIdOrSourcePatientPatientIdOrderByMergedAtDesc(
            UUID targetPatientId,
            UUID sourcePatientId
    );
}
