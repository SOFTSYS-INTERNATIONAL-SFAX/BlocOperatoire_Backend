package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.score.*;
import com.tn.softsys.blocoperatoire.service.ScoreService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService service;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public ScoreResponseDTO create(@Valid @RequestBody ScoreRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public ScoreResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<ScoreResponseDTO> search(
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {

        return service.search(interventionId, pageable);
    }
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ScoreResponseDTO update(@PathVariable UUID id,
                                   @RequestBody ScoreRequestDTO dto) {
        return service.update(id, dto);
    }
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}