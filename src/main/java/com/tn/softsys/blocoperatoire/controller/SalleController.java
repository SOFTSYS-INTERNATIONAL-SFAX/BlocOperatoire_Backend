package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.salle.SalleRequestDTO;
import com.tn.softsys.blocoperatoire.dto.salle.SalleResponseDTO;
import com.tn.softsys.blocoperatoire.service.SalleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleController {

    private final SalleService service;

    /* =====================================================
       CREATE
       ===================================================== */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO create(
            @Valid @RequestBody SalleRequestDTO dto) {

        return service.create(dto);
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody SalleRequestDTO dto) {

        return service.update(id, dto);
    }

    /* =====================================================
       PATCH ACTIVE
       ===================================================== */

    @PatchMapping("/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public SalleResponseDTO updateActive(
            @PathVariable UUID id,
            @RequestParam Boolean active) {

        return service.updateActive(id, active);
    }

    /* =====================================================
       READ ONE
       ===================================================== */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public SalleResponseDTO getById(@PathVariable UUID id) {

        return service.getById(id);
    }

    /* =====================================================
       SEARCH + PAGINATION
       ===================================================== */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<SalleResponseDTO> search(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String etageBatiment,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {

        return service.search(nom, etageBatiment, active, pageable);
    }

    /* =====================================================
       DELETE
       ===================================================== */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {

        service.delete(id);
    }
}