package com.tn.softsys.blocoperatoire.controller;
import com.tn.softsys.blocoperatoire.dto.surveillance.*;
import com.tn.softsys.blocoperatoire.service.SurveillanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
@RestController
@RequestMapping("/api/sspi/surveillances")
@RequiredArgsConstructor
public class SurveillanceController {

    private final SurveillanceService service;

    @PostMapping
    public SurveillanceResponseDTO create(@RequestBody SurveillanceRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<SurveillanceResponseDTO> findBySspi(
            @RequestParam UUID sspiId,
            Pageable pageable) {

        return service.findBySspi(sspiId, pageable);
    }
    @GetMapping("/{id}")
    public SurveillanceResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public SurveillanceResponseDTO update(
            @PathVariable UUID id,
            @RequestBody SurveillanceRequestDTO dto) {
        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}