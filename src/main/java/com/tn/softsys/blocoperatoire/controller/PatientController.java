package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import com.tn.softsys.blocoperatoire.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    /* ==========================
       CREATE
       ========================== */

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CADRE')")
    public PatientResponseDTO create(
            @Valid @RequestBody PatientRequestDTO dto) {

        return service.create(dto);
    }

    /* ==========================
       UPDATE
       ========================== */

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public PatientResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDTO dto) {

        return service.update(id, dto);
    }

    /* ==========================
       READ ONE
       ========================== */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public PatientResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    /* ==========================
       READ ALL + PAGINATION
       ========================== */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<PatientResponseDTO> getAll(
            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            Pageable pageable) {

        return service.search(nom, prenom, pageable);
    }

    /* ==========================
       DELETE
       ========================== */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}