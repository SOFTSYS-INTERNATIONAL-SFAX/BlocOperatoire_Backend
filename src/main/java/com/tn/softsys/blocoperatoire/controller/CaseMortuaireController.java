package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.casemor.*;
import com.tn.softsys.blocoperatoire.service.CaseMortuaireService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseMortuaireController {

    private final CaseMortuaireService service;

    // CREATE
    @PostMapping
    public CaseMortuaireResponseDTO create(
            @Valid @RequestBody CaseMortuaireRequestDTO dto) {
        return service.create(dto);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public CaseMortuaireResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // GET ALL + SEARCH + PAGINATION
    @GetMapping
    public Page<CaseMortuaireResponseDTO> search(
            @RequestParam(required = false) UUID morgueId,
            @RequestParam(required = false) Boolean occupee,
            Pageable pageable) {

        return service.search(morgueId, occupee, pageable);
    }

    // UPDATE
    @PutMapping("/{id}")
    public CaseMortuaireResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody CaseMortuaireRequestDTO dto) {

        return service.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    // AFFECTER
    @PutMapping("/{id}/affecter")
    public CaseMortuaireResponseDTO affecter(
            @PathVariable UUID id,
            @RequestParam UUID decesId) {

        return service.affecter(id, decesId);
    }

    // LIBERER
    @PutMapping("/{id}/liberer")
    public CaseMortuaireResponseDTO liberer(@PathVariable UUID id) {
        return service.liberer(id);
    }
}