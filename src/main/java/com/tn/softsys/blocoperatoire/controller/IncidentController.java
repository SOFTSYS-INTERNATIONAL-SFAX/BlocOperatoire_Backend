package com.tn.softsys.blocoperatoire.controller;
import com.tn.softsys.blocoperatoire.dto.scores.*;
import com.tn.softsys.blocoperatoire.dto.incident.*;
import com.tn.softsys.blocoperatoire.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/sspi/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @PostMapping
    public IncidentResponseDTO create(@RequestBody IncidentRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<IncidentResponseDTO> findBySspi(
            @RequestParam UUID sspiId,
            Pageable pageable) {

        return service.findBySspi(sspiId, pageable);
    }
    @GetMapping("/{id}")
    public IncidentResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public IncidentResponseDTO update(
            @PathVariable UUID id,
            @RequestBody IncidentRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}