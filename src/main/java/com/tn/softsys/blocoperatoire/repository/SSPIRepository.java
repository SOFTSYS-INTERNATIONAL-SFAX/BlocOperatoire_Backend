package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.SSPI;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SSPIRepository extends JpaRepository<SSPI, UUID> {

    Optional<SSPI> findByInterventionInterventionId(UUID interventionId);

    boolean existsByInterventionInterventionId(UUID interventionId);

    Page<SSPI> findByHeureEntreeBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
}