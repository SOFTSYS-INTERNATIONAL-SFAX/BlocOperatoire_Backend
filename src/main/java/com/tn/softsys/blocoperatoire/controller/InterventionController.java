package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import com.tn.softsys.blocoperatoire.dto.intervention.*;
import com.tn.softsys.blocoperatoire.service.InterventionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/interventions")
@RequiredArgsConstructor
public class InterventionController {

    private final InterventionService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','PLANNING_WRITE')")
    public InterventionResponseDTO create(
            @Valid @RequestBody InterventionRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','PLANNING_WRITE')")
    public InterventionResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody InterventionRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PLANNING_READ')")
    public InterventionResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PLANNING_READ')")
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

    @PatchMapping("/{id}/statut")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','PLANNING_WRITE','OMS_VALIDATE')")
    public ResponseEntity<InterventionResponseDTO> updateStatut(
            @PathVariable UUID id,
            @Valid @RequestBody InterventionStatutPatchDTO dto) {

        return ResponseEntity.ok(service.updateStatut(id, dto.getStatut()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
