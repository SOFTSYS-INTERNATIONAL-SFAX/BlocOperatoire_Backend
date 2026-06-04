package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.Deces;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface DecesRepository extends JpaRepository<Deces, UUID>, JpaSpecificationExecutor<Deces> {

    boolean existsByInterventionInterventionId(UUID interventionId);
}
