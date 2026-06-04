package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.morgue.MorgueRequestDTO;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueResponseDTO;
import com.tn.softsys.blocoperatoire.service.MorgueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/morgues")
@RequiredArgsConstructor
public class MorgueController {

    private final MorgueService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MorgueResponseDTO create(@Valid @RequestBody MorgueRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public MorgueResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public Page<MorgueResponseDTO> search(
            @RequestParam(required = false) String nom,
            Pageable pageable) {

        return service.search(nom, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public MorgueResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody MorgueRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
