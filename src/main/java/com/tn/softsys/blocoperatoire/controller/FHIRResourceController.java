package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.fhir.*;
import com.tn.softsys.blocoperatoire.service.FHIRResourceService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/fhir-resources")
@RequiredArgsConstructor
public class FHIRResourceController {

    private final FHIRResourceService service;

    @PostMapping
    public FHIRResourceResponseDTO create(
            @RequestBody FHIRResourceRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping
    public Page<FHIRResourceResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {

        return service.search(patientId, interventionId, pageable);
    }
}