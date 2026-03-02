package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.tempsoperatoire.*;
import com.tn.softsys.blocoperatoire.service.TempsOperatoireService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tn.softsys.blocoperatoire.domain.*;


import java.time.LocalDateTime;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@RestController
@RequestMapping("/api/temps-operatoires")
@RequiredArgsConstructor
public class TempsOperatoireController {

    private final TempsOperatoireService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public TempsOperatoireResponseDTO create(
            @RequestBody TempsOperatoireRequestDTO dto) {

        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public TempsOperatoireResponseDTO getById(@PathVariable UUID id) {

        return service.getById(id);
    }

    @GetMapping("/intervention/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public TempsOperatoireResponseDTO getByIntervention(
            @PathVariable UUID id) {

        return service.getByIntervention(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public Page<TempsOperatoireResponseDTO> search(
            @RequestParam(required = false) UUID interventionId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable) {

        return service.search(interventionId, from, to, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public TempsOperatoireResponseDTO update(
            @PathVariable UUID id,
            @RequestBody TempsOperatoireRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}