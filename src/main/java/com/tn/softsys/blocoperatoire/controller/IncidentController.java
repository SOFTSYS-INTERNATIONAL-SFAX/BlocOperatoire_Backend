package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.incident.IncidentCreateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResolveRequestDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResponseDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentUpdateRequestDTO;
import com.tn.softsys.blocoperatoire.service.IncidentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sspi")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService service;

    @PostMapping("/{sspiId}/incidents")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public IncidentResponseDTO create(
            @PathVariable UUID sspiId,
            @Valid @RequestBody IncidentCreateRequestDTO dto) {
        return service.create(sspiId, dto);
    }

    @GetMapping("/{sspiId}/incidents")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public Page<IncidentResponseDTO> findBySspi(
            @PathVariable UUID sspiId,
            Pageable pageable) {
        return service.findBySspi(sspiId, pageable);
    }

    @GetMapping("/incidents/{id}")
    @PreAuthorize("hasAuthority('PATIENT_READ')")
    public IncidentResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @PutMapping("/incidents/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public IncidentResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody IncidentUpdateRequestDTO dto) {
        return service.update(id, dto);
    }

    @PatchMapping("/incidents/{id}/resolve")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public IncidentResponseDTO resolve(
            @PathVariable UUID id,
            @RequestBody IncidentResolveRequestDTO dto) {
        return service.resolve(id, dto);
    }

    @DeleteMapping("/incidents/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_CHIRURGIEN','ROLE_ANESTHESISTE','ROLE_REANIMATEUR','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
