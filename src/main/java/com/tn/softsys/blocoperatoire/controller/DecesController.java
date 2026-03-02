package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.deces.*;
import com.tn.softsys.blocoperatoire.service.DecesService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/deces")
@RequiredArgsConstructor
public class DecesController {

    private final DecesService service;

    @PostMapping
    public DecesResponseDTO create(
            @Valid @RequestBody DecesRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public DecesResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<DecesResponseDTO> search(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable) {

        return service.search(from, to, pageable);
    }

    @PutMapping("/{id}")
    public DecesResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody DecesRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}