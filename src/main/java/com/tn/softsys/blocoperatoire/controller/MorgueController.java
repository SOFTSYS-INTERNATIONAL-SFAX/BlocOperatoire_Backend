package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.morgue.*;
import com.tn.softsys.blocoperatoire.service.MorgueService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/morgues")
@RequiredArgsConstructor
public class MorgueController {

    private final MorgueService service;

    @PostMapping
    public MorgueResponseDTO create(@Valid @RequestBody MorgueRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public MorgueResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<MorgueResponseDTO> search(
            @RequestParam(required = false) String nom,
            Pageable pageable) {

        return service.search(nom, pageable);
    }

    @PutMapping("/{id}")
    public MorgueResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody MorgueRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}