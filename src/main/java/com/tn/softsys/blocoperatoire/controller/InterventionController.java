package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import com.tn.softsys.blocoperatoire.dto.intervention.*;
import com.tn.softsys.blocoperatoire.service.InterventionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/interventions")
@RequiredArgsConstructor
public class InterventionController {

    private final InterventionService service;

    /* ================= CREATE ================= */

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public InterventionResponseDTO create(
            @Valid @RequestBody InterventionRequestDTO dto) {
        return service.create(dto);
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public InterventionResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody InterventionRequestDTO dto) {
        return service.update(id, dto);
    }

    /* ================= READ ONE ================= */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public InterventionResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    /* ================= SEARCH ================= */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<InterventionResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) StatutIntervention statut,
            @RequestParam(required = false) Boolean urgenceOMS,
            @RequestParam(required = false) String codeActe,
            Pageable pageable
    ) {
        return service.search(
                patientId,
                statut,
                urgenceOMS,
                codeActe,
                pageable
        );
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}