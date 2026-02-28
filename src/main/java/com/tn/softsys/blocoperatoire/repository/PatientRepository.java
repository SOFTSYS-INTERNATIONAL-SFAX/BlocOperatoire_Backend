package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {

    // ===============================
    // Recherche FHIR
    // ===============================
    Optional<Patient> findByIdentiteFHIR(String identiteFHIR);

    boolean existsByIdentiteFHIR(String identiteFHIR);

    // ===============================
    // Pagination + Recherche
    // ===============================

    Page<Patient> findByNomContainingIgnoreCase(String nom, Pageable pageable);

    Page<Patient> findByPrenomContainingIgnoreCase(String prenom, Pageable pageable);

    Page<Patient> findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(
            String nom,
            String prenom,
            Pageable pageable
    );

    Page<Patient> findByDateNaissance(LocalDate dateNaissance, Pageable pageable);

}