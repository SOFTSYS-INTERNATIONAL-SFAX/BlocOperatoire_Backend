package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementRequestDTO;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementResponseDTO;
import com.tn.softsys.blocoperatoire.service.ConsentementService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/consentements")
@RequiredArgsConstructor
public class ConsentementController {

    private final ConsentementService service;

    @PostMapping
    public ConsentementResponseDTO create(
            @Valid @RequestBody ConsentementRequestDTO dto) {

        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ConsentementResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<ConsentementResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {

        return service.search(patientId, interventionId, pageable);
    }

    @PutMapping("/{id}")
    public ConsentementResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody ConsentementRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}