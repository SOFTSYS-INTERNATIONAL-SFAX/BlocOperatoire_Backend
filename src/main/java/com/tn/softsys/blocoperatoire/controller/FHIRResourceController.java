package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.fhir.*;
import com.tn.softsys.blocoperatoire.service.FHIRResourceService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fhir-resources")
@RequiredArgsConstructor
public class FHIRResourceController {

    private final FHIRResourceService service;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATEUR_SYSTEME','ROLE_DIRECTION_MEDICALE','ROLE_RESPONSABLE_QUALITE','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR')")
    @PostMapping
    public FHIRResourceResponseDTO create(
            @Valid @RequestBody FHIRResourceRequestDTO dto) {
        return service.create(dto);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_ADMINISTRATEUR_SYSTEME','ROLE_DIRECTION_MEDICALE','ROLE_RESPONSABLE_QUALITE','ROLE_CADRE_BLOC','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    @GetMapping
    public Page<FHIRResourceResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {

        return service.search(patientId, interventionId, pageable);
    }
}
