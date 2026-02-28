package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.reanimation.ReanimationRequestDTO;
import com.tn.softsys.blocoperatoire.dto.reanimation.ReanimationResponseDTO;
import com.tn.softsys.blocoperatoire.service.ReanimationService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/reanimations")
@RequiredArgsConstructor
public class ReanimationController {

    private final ReanimationService service;

    /* ================= CREATE ================= */

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public ReanimationResponseDTO create(
            @RequestBody ReanimationRequestDTO dto) {

        return service.create(dto);
    }

    /* ================= GET BY ID ================= */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public ReanimationResponseDTO getById(
            @PathVariable UUID id) {

        return service.getById(id);
    }

    /* ================= SEARCH ================= */

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public Page<ReanimationResponseDTO> search(
            @RequestParam(required = false) UUID sspiId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) Boolean enCours,
            @PageableDefault(size = 10) Pageable pageable) {

        return service.search(sspiId, from, to, enCours, pageable);
    }

    /* ================= UPDATE ================= */

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public ReanimationResponseDTO update(
            @PathVariable UUID id,
            @RequestBody ReanimationRequestDTO dto) {

        return service.update(id, dto);
    }

    /* ================= DELETE ================= */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {

        service.delete(id);
    }
}