package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends
        JpaRepository<Patient, UUID>,
        JpaSpecificationExecutor<Patient> {

    /* ================= IDENTIFICATION ================= */

    Optional<Patient> findByIdentiteFHIR(String identiteFHIR);
    boolean existsByIdentiteFHIR(String identiteFHIR);

    Optional<Patient> findByMrn(String mrn);
    boolean existsByMrn(String mrn);

    /* ================= STATISTIQUES ================= */

    long countBySexe(Sexe sexe);
    long countByGroupeSanguin(GroupeSanguin groupeSanguin);

    /* ================= LISTES RAPIDES ================= */

    List<Patient> findTop10ByOrderByCreatedAtDesc();
    List<Patient> findTop10ByOrderByUpdatedAtDesc();
}