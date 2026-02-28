package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.parametrerea.ParametreReaRequestDTO;
import com.tn.softsys.blocoperatoire.dto.parametrerea.ParametreReaResponseDTO;
import com.tn.softsys.blocoperatoire.service.ParametreReaService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.format.annotation.DateTimeFormat;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/parametres-rea")
@RequiredArgsConstructor
public class ParametreReaController {

    private final ParametreReaService service;

    /* ================= CREATE ================= */

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public ParametreReaResponseDTO create(
            @RequestBody ParametreReaRequestDTO dto) {

        return service.create(dto);
    }

    /* ================= GET BY ID ================= */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public ParametreReaResponseDTO getById(
            @PathVariable UUID id) {

        return service.getById(id);
    }

    /* ================= SEARCH ================= */

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public Page<ParametreReaResponseDTO> search(
            @RequestParam(required = false) UUID reaId,
            @RequestParam(required = false) Boolean ventilation,
            @RequestParam(required = false) Boolean vasopresseurActif,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            @PageableDefault(size = 10) Pageable pageable) {

        return service.search(
                reaId,
                ventilation,
                vasopresseurActif,
                from,
                to,
                pageable
        );
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public ParametreReaResponseDTO update(
            @PathVariable UUID id,
            @RequestBody ParametreReaRequestDTO dto) {

        return service.update(id, dto);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {

        service.delete(id);
    }
}