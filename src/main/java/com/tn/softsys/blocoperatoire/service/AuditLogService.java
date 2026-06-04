package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.AuditLog;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    /* ===================================================== */
    /* ======================= LOG SUCCESS ================== */
    /* ===================================================== */

    public void log(User user,
                    String action,
                    String module,
                    UUID referenceId,
                    String details,
                    String ipAddress) {

        AuditLog audit = AuditLog.builder()
                .user(user)
                .action(action)
                .module(module)
                .referenceId(referenceId)
                .ipAddress(ipAddress)
                .details(details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(audit);
    }

    public void logFailedAttempt(String usernameAttempt,
                                 String action,
                                 String module,
                                 String details,
                                 String ipAddress) {

        AuditLog audit = AuditLog.builder()
                .user(null)
                .action(action)
                .module(module)
                .referenceId(null)
                .ipAddress(ipAddress)
                .details("Attempted username=" + usernameAttempt + " | " + details)
                .timestamp(LocalDateTime.now())
                .build();

        auditLogRepository.save(audit);
    }

    /* ===================================================== */
    /* ======================= READ ========================= */
    /* ===================================================== */

    @Transactional(readOnly = true)
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByUser(UUID userId, Pageable pageable) {
        return auditLogRepository.findByUserUserId(userId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByModule(String module, Pageable pageable) {
        return auditLogRepository.findByModuleIgnoreCase(module, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByAction(String action, Pageable pageable) {
        return auditLogRepository.findByActionIgnoreCase(action, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByReference(UUID referenceId, Pageable pageable) {
        return auditLogRepository.findByReferenceId(referenceId, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> findByPeriod(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable) {

        return auditLogRepository.findByTimestampBetween(start, end, pageable);
    }

    @Transactional(readOnly = true)
    public Page<AuditLog> searchInDetails(
            String keyword,
            Pageable pageable) {

        return auditLogRepository.searchInDetails(keyword, pageable);
    }
}