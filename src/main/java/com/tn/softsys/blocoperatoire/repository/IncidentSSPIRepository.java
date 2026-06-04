package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IncidentSSPIRepository extends JpaRepository<IncidentSSPI, UUID> {

    Page<IncidentSSPI> findBySspiSspiIdOrderByDeclaredAtDesc(UUID sspiId, Pageable pageable);

    Optional<IncidentSSPI> findFirstBySspiSspiIdAndTypeAndResoluFalse(UUID sspiId, String type);

    @EntityGraph(attributePaths = {"sspi", "sspi.intervention", "sspi.intervention.patient"})
    List<IncidentSSPI> findBySspi_SspiIdIn(Collection<UUID> sspiIds);

    @EntityGraph(attributePaths = {"sspi", "sspi.intervention", "sspi.intervention.patient"})
    List<IncidentSSPI> findByDeclaredAtBetweenOrderByDeclaredAtDesc(LocalDateTime from, LocalDateTime to);
}
