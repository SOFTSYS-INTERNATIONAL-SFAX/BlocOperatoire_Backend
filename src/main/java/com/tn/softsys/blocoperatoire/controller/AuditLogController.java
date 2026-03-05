package com.tn.softsys.blocoperatoire.web.controller;

import com.tn.softsys.blocoperatoire.domain.AuditLog;
import com.tn.softsys.blocoperatoire.service.AuditLogService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/audit")
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ROLE_ADMIN')")
public class AuditLogController {

    private final AuditLogService auditLogService;

    /* ===================================================== */
    /* ===================== GLOBAL READ ==================== */
    /* ===================================================== */

    @GetMapping
    public Page<AuditLog> findAll(Pageable pageable) {
        return auditLogService.findAll(pageable);
    }

    /* ===================================================== */
    /* ===================== FILTERS ======================== */
    /* ===================================================== */

    @GetMapping("/user/{userId}")
    public Page<AuditLog> findByUser(
            @PathVariable UUID userId,
            Pageable pageable) {

        return auditLogService.findByUser(userId, pageable);
    }

    @GetMapping("/module/{module}")
    public Page<AuditLog> findByModule(
            @PathVariable String module,
            Pageable pageable) {

        return auditLogService.findByModule(module, pageable);
    }

    @GetMapping("/action/{action}")
    public Page<AuditLog> findByAction(
            @PathVariable String action,
            Pageable pageable) {

        return auditLogService.findByAction(action, pageable);
    }

    @GetMapping("/reference/{referenceId}")
    public Page<AuditLog> findByReference(
            @PathVariable UUID referenceId,
            Pageable pageable) {

        return auditLogService.findByReference(referenceId, pageable);
    }

    /* ===================================================== */
    /* ===================== PERIOD ========================= */
    /* ===================================================== */

    @GetMapping("/period")
    public Page<AuditLog> findByPeriod(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end,

            Pageable pageable) {

        return auditLogService.findByPeriod(start, end, pageable);
    }

    /* ===================================================== */
    /* ===================== SEARCH ========================= */
    /* ===================================================== */

    @GetMapping("/search")
    public Page<AuditLog> searchInDetails(
            @RequestParam String keyword,
            Pageable pageable) {

        return auditLogService.searchInDetails(keyword, pageable);
    }
}