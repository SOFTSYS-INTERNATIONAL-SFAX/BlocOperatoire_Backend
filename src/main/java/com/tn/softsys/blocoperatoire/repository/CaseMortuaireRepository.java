package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface CaseMortuaireRepository
        extends JpaRepository<CaseMortuaire, UUID>, JpaSpecificationExecutor<CaseMortuaire> {

    boolean existsByNumeroCase(String numeroCase);

    boolean existsByNumeroCaseAndCaseIdNot(String numeroCase, UUID caseId);

    boolean existsByDecesDecesId(UUID decesId);
}
