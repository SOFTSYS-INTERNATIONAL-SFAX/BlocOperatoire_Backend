package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.sspi.SSPIClotureRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIResponseDTO;
import com.tn.softsys.blocoperatoire.service.SSPIService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public SSPIResponseDTO create(@Valid @RequestBody SSPIRequestDTO dto) {
        return service.create(dto);
    }

    @PutMapping("/{id}/close")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE')")
    public SSPIResponseDTO close(
            @PathVariable UUID id,
            @RequestBody SSPIClotureRequestDTO dto) {
        return service.close(id, dto);
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public Page<SSPIResponseDTO> search(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime to,
            Pageable pageable) {

        return service.search(from, to, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public SSPIResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
