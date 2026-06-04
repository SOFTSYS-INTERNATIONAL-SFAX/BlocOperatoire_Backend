package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.alert.AlertSettingsRequestDTO;
import com.tn.softsys.blocoperatoire.dto.alert.AlertSettingsResponseDTO;
import com.tn.softsys.blocoperatoire.service.AlertSettingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alerts/settings")
@RequiredArgsConstructor
public class AlertSettingsController {

    private final AlertSettingsService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public AlertSettingsResponseDTO getSettings() {
        return service.getSettings();
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public AlertSettingsResponseDTO updateSettings(
            @Valid @RequestBody AlertSettingsRequestDTO dto) {

        return service.updateSettings(dto);
    }
}
