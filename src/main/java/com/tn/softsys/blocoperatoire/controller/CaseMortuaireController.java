package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireRequestDTO;
import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireResponseDTO;
import com.tn.softsys.blocoperatoire.service.CaseMortuaireService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/cases")
@RequiredArgsConstructor
public class CaseMortuaireController {

    private final CaseMortuaireService service;

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CaseMortuaireResponseDTO create(
            @Valid @RequestBody CaseMortuaireRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public CaseMortuaireResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public Page<CaseMortuaireResponseDTO> search(
            @RequestParam(required = false) UUID morgueId,
            @RequestParam(required = false) Boolean occupee,
            @RequestParam(required = false) String q,
            Pageable pageable) {

        return service.search(morgueId, occupee, q, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public CaseMortuaireResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody CaseMortuaireRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    @PutMapping("/{id}/affecter")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public CaseMortuaireResponseDTO affecter(
            @PathVariable UUID id,
            @RequestParam UUID decesId) {

        return service.affecter(id, decesId);
    }

    @PutMapping("/{id}/liberer")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public CaseMortuaireResponseDTO liberer(@PathVariable UUID id) {
        return service.liberer(id);
    }
}
