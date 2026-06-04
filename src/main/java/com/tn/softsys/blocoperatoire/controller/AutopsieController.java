package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.AutopsieStatut;
import com.tn.softsys.blocoperatoire.dto.autopsie.AutopsieRequestDTO;
import com.tn.softsys.blocoperatoire.dto.autopsie.AutopsieResponseDTO;
import com.tn.softsys.blocoperatoire.service.AutopsieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/autopsies")
@RequiredArgsConstructor
public class AutopsieController {

    private final AutopsieService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public AutopsieResponseDTO create(@Valid @RequestBody AutopsieRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public AutopsieResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public Page<AutopsieResponseDTO> search(
            @RequestParam(required = false) UUID decesId,
            @RequestParam(required = false) UUID morgueId,
            @RequestParam(required = false) AutopsieStatut statut,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String q,
            Pageable pageable) {

        return service.search(decesId, morgueId, statut, from, to, q, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public AutopsieResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody AutopsieRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
