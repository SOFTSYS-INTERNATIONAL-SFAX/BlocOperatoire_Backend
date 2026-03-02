package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.salle.*;
import com.tn.softsys.blocoperatoire.service.SalleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService service;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO create(@Valid @RequestBody SalleRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody SalleRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/{id}/disponibilite")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO updateDisponibilite(
            @PathVariable UUID id,
            @RequestParam Boolean disponible) {

        return service.updateDisponibilite(id, disponible);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public SalleResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<SalleResponseDTO> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean disponible,
            Pageable pageable) {

        return service.search(nom, type, disponible, pageable);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}