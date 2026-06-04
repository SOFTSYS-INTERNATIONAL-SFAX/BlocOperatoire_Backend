package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.deces.DecesRequestDTO;
import com.tn.softsys.blocoperatoire.dto.deces.DecesResponseDTO;
import com.tn.softsys.blocoperatoire.service.DecesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/deces")
@RequiredArgsConstructor
public class DecesController {

    private final DecesService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public DecesResponseDTO create(
            @Valid @RequestBody DecesRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public DecesResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public Page<DecesResponseDTO> search(
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) UUID interventionId,
            @RequestParam(required = false) Boolean nonAffecte,
            @RequestParam(required = false) String q,
            Pageable pageable) {

        return service.search(from, to, interventionId, nonAffecte, q, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public DecesResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody DecesRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
