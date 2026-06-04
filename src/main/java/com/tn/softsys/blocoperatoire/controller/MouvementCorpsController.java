package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsRequestDTO;
import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsResponseDTO;
import com.tn.softsys.blocoperatoire.service.MouvementCorpsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/mouvements")
@RequiredArgsConstructor
public class MouvementCorpsController {

    private final MouvementCorpsService service;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE','ROLE_MEDECIN')")
    public MouvementCorpsResponseDTO create(
            @Valid @RequestBody MouvementCorpsRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public MouvementCorpsResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public Page<MouvementCorpsResponseDTO> search(
            @RequestParam(required = false) UUID caseId,
            @RequestParam(required = false) UUID morgueId,
            @RequestParam(required = false) UUID decesId,
            @RequestParam(required = false) LocalDateTime from,
            @RequestParam(required = false) LocalDateTime to,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String q,
            Pageable pageable) {

        return service.search(caseId, morgueId, decesId, from, to, type, q, pageable);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE','ROLE_MEDECIN')")
    public MouvementCorpsResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody MouvementCorpsRequestDTO dto) {

        return service.update(id, dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }
}
