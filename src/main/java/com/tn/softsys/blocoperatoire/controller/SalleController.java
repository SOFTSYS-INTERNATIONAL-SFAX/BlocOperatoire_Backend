package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.salle.SalleRequestDTO;
import com.tn.softsys.blocoperatoire.dto.salle.SalleResponseDTO;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import com.tn.softsys.blocoperatoire.service.AuditLogService;
import com.tn.softsys.blocoperatoire.service.SalleService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService service;
    private final AuditLogService auditLogService;
    private final UserRepository userRepository;
    private final HttpServletRequest httpRequest;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO create(@Valid @RequestBody SalleRequestDTO dto) {
        SalleResponseDTO created = service.create(dto);

        auditLogService.log(
                resolveCurrentUser(),
                "CREATION",
                "SALLE",
                created.getSalleId(),
                "Creation salle: " + created.getNom(),
                resolveClientIp()
        );

        return created;
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO update(@PathVariable UUID id, @Valid @RequestBody SalleRequestDTO dto) {
        SalleResponseDTO updated = service.update(id, dto);

        auditLogService.log(
                resolveCurrentUser(),
                "MODIFICATION",
                "SALLE",
                id,
                "Modification salle: " + updated.getNom(),
                resolveClientIp()
        );

        return updated;
    }

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO updateActive(@PathVariable UUID id, @RequestParam Boolean active) {
        SalleResponseDTO updated = service.updateActive(id, active);

        auditLogService.log(
                resolveCurrentUser(),
                "MODIFICATION",
                "SALLE",
                id,
                "Mise a jour statut salle: active=" + active + " | " + updated.getNom(),
                resolveClientIp()
        );

        return updated;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLANNING_READ')")
    public SalleResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLANNING_READ')")
    public Page<SalleResponseDTO> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String etageBatiment,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {

        return service.search(nom, etageBatiment, active, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        SalleResponseDTO salle = service.getById(id);
        service.delete(id);

        auditLogService.log(
                resolveCurrentUser(),
                "SUPPRESSION",
                "SALLE",
                id,
                "Suppression salle: " + salle.getNom(),
                resolveClientIp()
        );
    }

    private User resolveCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        String email = auth.getName();
        if (email == null || email.isBlank() || "anonymousUser".equalsIgnoreCase(email)) {
            return null;
        }

        return userRepository.findByEmail(email).orElse(null);
    }

    private String resolveClientIp() {
        String xForwardedFor = httpRequest.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = httpRequest.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isBlank()) {
            return xRealIp.trim();
        }

        return httpRequest.getRemoteAddr();
    }
}
