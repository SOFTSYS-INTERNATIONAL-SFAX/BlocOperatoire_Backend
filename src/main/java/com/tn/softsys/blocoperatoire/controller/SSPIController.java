package com.tn.softsys.blocoperatoire.controller;
import com.tn.softsys.blocoperatoire.dto.score.*;
import com.tn.softsys.blocoperatoire.dto.sspi.*;
import com.tn.softsys.blocoperatoire.service.SSPIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;
@RestController
@RequestMapping("/api/sspi")
@RequiredArgsConstructor
public class SSPIController {

    private final SSPIService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public SSPIResponseDTO create(@RequestBody SSPIRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public SSPIResponseDTO close(
            @PathVariable UUID id,
            @RequestParam LocalDateTime sortie) {

        return service.close(id, sortie);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER')")
    public Page<SSPIResponseDTO> search(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            Pageable pageable) {

        return service.search(from, to, pageable);
    }
    @GetMapping("/{id}")
    public SSPIResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}