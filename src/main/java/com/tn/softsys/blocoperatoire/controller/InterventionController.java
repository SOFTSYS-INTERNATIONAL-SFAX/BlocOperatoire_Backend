package com.tn.softsys.blocoperatoire.controller;

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

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public InterventionResponseDTO create(@Valid @RequestBody InterventionRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public InterventionResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody InterventionRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public InterventionResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<InterventionResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) String statut,
            @RequestParam(required = false) Boolean urgence,
            @RequestParam(required = false) String codeActe,
            Pageable pageable) {

        return service.search(patientId, statut, urgence, codeActe, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}