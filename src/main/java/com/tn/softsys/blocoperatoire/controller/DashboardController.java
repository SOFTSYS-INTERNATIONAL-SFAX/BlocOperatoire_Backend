package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.dashboard.DashboardOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATEUR_SYSTEME','ROLE_DIRECTION_MEDICALE','ROLE_RESPONSABLE_QUALITE','ROLE_CADRE_BLOC','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public DashboardOverviewResponseDTO getOverview(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date
    ) {
        return dashboardService.getOverview(date);
    }
}
