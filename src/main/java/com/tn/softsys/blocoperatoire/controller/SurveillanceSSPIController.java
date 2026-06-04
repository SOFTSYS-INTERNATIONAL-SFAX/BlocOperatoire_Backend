package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.sspi.SurveillanceSSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SurveillanceSSPIResponseDTO;
import com.tn.softsys.blocoperatoire.service.SurveillanceSSPIService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sspi")
@RequiredArgsConstructor
public class SurveillanceSSPIController {

    private final SurveillanceSSPIService service;

    @PostMapping("/{sspiId}/surveillances")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public SurveillanceSSPIResponseDTO create(
            @PathVariable UUID sspiId,
            @RequestBody SurveillanceSSPIRequestDTO dto) {
        return service.create(sspiId, dto);
    }

    @GetMapping("/{sspiId}/surveillances")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public Page<SurveillanceSSPIResponseDTO> findBySspi(
            @PathVariable UUID sspiId,
            Pageable pageable) {
        return service.findBySspi(sspiId, pageable);
    }

    @GetMapping("/surveillances/{id}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public SurveillanceSSPIResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/surveillances/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public SurveillanceSSPIResponseDTO update(
            @PathVariable UUID id,
            @RequestBody SurveillanceSSPIRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/surveillances/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
