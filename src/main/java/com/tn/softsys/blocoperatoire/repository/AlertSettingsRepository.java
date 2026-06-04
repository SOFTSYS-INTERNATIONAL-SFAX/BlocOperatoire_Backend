package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.AlertSettings;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AlertSettingsRepository extends JpaRepository<AlertSettings, UUID> {

    Optional<AlertSettings> findTopByOrderByCreatedAtAsc();
}
