package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.sspi.TraitementSSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.TraitementSSPIResponseDTO;
import com.tn.softsys.blocoperatoire.service.TraitementSSPIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sspi")
@RequiredArgsConstructor
public class TraitementSSPIController {

    private final TraitementSSPIService service;

    @PostMapping("/{sspiId}/traitements")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public TraitementSSPIResponseDTO create(
            @PathVariable UUID sspiId,
            @Valid @RequestBody TraitementSSPIRequestDTO dto) {
        return service.create(sspiId, dto);
    }

    @GetMapping("/{sspiId}/traitements")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public Page<TraitementSSPIResponseDTO> findBySspi(
            @PathVariable UUID sspiId,
            Pageable pageable) {
        return service.findBySspi(sspiId, pageable);
    }
}
