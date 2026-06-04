package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.alert.AlertResponseDTO;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import com.tn.softsys.blocoperatoire.service.AlertService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;
    private final UserRepository userRepository;

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public List<AlertResponseDTO> getActive() {
        return alertService.findActiveAlerts();
    }

    @GetMapping("/history")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public Page<AlertResponseDTO> getHistory(
            @PageableDefault(size = 40, sort = "createdAt", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        return alertService.findAlertHistory(pageable);
    }

    @PatchMapping("/{id}/ack")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public AlertResponseDTO acknowledge(@PathVariable UUID id, HttpServletRequest request) {
        return alertService.acknowledge(id, resolveCurrentUser(), resolveClientIp(request));
    }

    @PatchMapping("/{id}/read")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public AlertResponseDTO markAsRead(@PathVariable UUID id, HttpServletRequest request) {
        return alertService.markAsRead(id, resolveCurrentUser(), resolveClientIp(request));
    }

    @PatchMapping("/read-all")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public long markAllAsRead(HttpServletRequest request) {
        return alertService.markAllAsRead(resolveCurrentUser(), resolveClientIp(request));
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

    private String resolveClientIp(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }

        return request.getRemoteAddr();
    }
}
