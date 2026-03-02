package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    List<AuditLog> findByUserUserId(UUID userId);
}
