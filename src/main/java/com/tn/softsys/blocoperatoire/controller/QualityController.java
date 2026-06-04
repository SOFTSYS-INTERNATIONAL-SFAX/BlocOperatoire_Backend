package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.quality.QualityOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.service.AuditContextService;
import com.tn.softsys.blocoperatoire.service.AuditLogService;
import com.tn.softsys.blocoperatoire.service.QualityService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/quality")
@RequiredArgsConstructor
public class QualityController {

    private final QualityService qualityService;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @GetMapping("/overview")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATEUR_SYSTEME','ROLE_DIRECTION_MEDICALE','ROLE_RESPONSABLE_QUALITE','ROLE_CADRE_BLOC','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public QualityOverviewResponseDTO getOverview(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(required = false)
            String bloc,
            @RequestParam(required = false, name = "specialite")
            String specialty
    ) {
        QualityOverviewResponseDTO response = qualityService.getOverview(date, bloc, specialty);

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "CONSULTATION",
                "QUALITE",
                null,
                "Consultation tableau qualite | date=" + response.referenceDate()
                        + " | bloc=" + (bloc != null ? bloc : "ALL")
                        + " | specialite=" + (specialty != null ? specialty : "ALL"),
                auditContextService.getClientIp()
        );

        return response;
    }

    @PostMapping("/export-audit")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATEUR_SYSTEME','ROLE_DIRECTION_MEDICALE','ROLE_RESPONSABLE_QUALITE','ROLE_CADRE_BLOC','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public void logExport(
            @RequestParam(required = false)
            String date,
            @RequestParam(required = false)
            String section,
            @RequestParam(required = false)
            String format
    ) {
        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "EXPORT",
                "QUALITE",
                null,
                "Export tableau qualite | date=" + (date != null && !date.isBlank() ? date : "AUTO")
                        + " | section=" + (section != null && !section.isBlank() ? section : "overview")
                        + " | format=" + (format != null && !format.isBlank() ? format : "PDF"),
                auditContextService.getClientIp()
        );
    }
}