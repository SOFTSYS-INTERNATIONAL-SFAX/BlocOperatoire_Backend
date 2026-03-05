package com.tn.softsys.blocoperatoire.repository;

import com.tn.softsys.blocoperatoire.domain.AuditLog;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {

    /* ================= USER ================= */

    Page<AuditLog> findByUserUserId(UUID userId, Pageable pageable);

    Page<AuditLog> findByUserIsNull(Pageable pageable);

    /* ================= MODULE ================= */

    Page<AuditLog> findByModule(String module, Pageable pageable);

    Page<AuditLog> findByModuleIgnoreCase(String module, Pageable pageable);

    /* ================= ACTION ================= */

    Page<AuditLog> findByAction(String action, Pageable pageable);

    Page<AuditLog> findByActionIgnoreCase(String action, Pageable pageable);

    /* ================= REFERENCE ================= */

    Page<AuditLog> findByReferenceId(UUID referenceId, Pageable pageable);

    /* ================= PERIOD ================= */

    Page<AuditLog> findByTimestampBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    /* ================= COMBINED FILTERS ================= */

    Page<AuditLog> findByModuleAndAction(
            String module,
            String action,
            Pageable pageable
    );

    Page<AuditLog> findByModuleAndTimestampBetween(
            String module,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<AuditLog> findByActionAndTimestampBetween(
            String action,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<AuditLog> findByUserUserIdAndTimestampBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    /* ================= SEARCH DETAILS (FULL TEXT SIMPLE) ================= */

    @Query("""
        SELECT a FROM AuditLog a
        WHERE LOWER(a.details) LIKE LOWER(CONCAT('%', :keyword, '%'))
    """)
    Page<AuditLog> searchInDetails(String keyword, Pageable pageable);

    /* ================= LAST EVENT ================= */

    Optional<AuditLog> findTopByOrderByTimestampDesc();

    Optional<AuditLog> findTopByUserUserIdOrderByTimestampDesc(UUID userId);

    Optional<AuditLog> findTopByReferenceIdOrderByTimestampDesc(UUID referenceId);
}