package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.mouvement.*;
import com.tn.softsys.blocoperatoire.service.MouvementCorpsService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementCorpsController {

    private final MouvementCorpsService service;

    // CREATE
    @PostMapping
    public MouvementCorpsResponseDTO create(
            @Valid @RequestBody MouvementCorpsRequestDTO dto) {
        return service.create(dto);
    }

    // GET BY ID
    @GetMapping("/{id}")
    public MouvementCorpsResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    // GET ALL + SEARCH + PAGINATION
    @GetMapping
    public Page<MouvementCorpsResponseDTO> search(
            @RequestParam(required = false) UUID caseId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String type,
            Pageable pageable) {

        return service.search(caseId, from, to, type, pageable);
    }

    // UPDATE
    @PutMapping("/{id}")
    public MouvementCorpsResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody MouvementCorpsRequestDTO dto) {

        return service.update(id, dto);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}