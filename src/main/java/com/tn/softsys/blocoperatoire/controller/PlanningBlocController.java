package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.planning.PlanningBlocRequestDTO;
import com.tn.softsys.blocoperatoire.dto.planning.PlanningBlocResponseDTO;
import com.tn.softsys.blocoperatoire.service.PlanningBlocService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/plannings")
@RequiredArgsConstructor
public class PlanningBlocController {

    private final PlanningBlocService service;

    @PostMapping
    public PlanningBlocResponseDTO create(
            @Valid @RequestBody PlanningBlocRequestDTO dto) {

        return service.create(dto);
    }

    @GetMapping("/{id}")
    public PlanningBlocResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<PlanningBlocResponseDTO> search(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable) {

        return service.search(from, to, pageable);
    }

    @PutMapping("/{id}")
    public PlanningBlocResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody PlanningBlocRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}