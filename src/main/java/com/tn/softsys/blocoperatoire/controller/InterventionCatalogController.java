package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.interventioncatalog.InterventionCatalogResponseDTO;
import com.tn.softsys.blocoperatoire.service.InterventionCatalogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/intervention-catalog")
@RequiredArgsConstructor
public class InterventionCatalogController {

    private final InterventionCatalogService service;

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<InterventionCatalogResponseDTO> search(
            @RequestParam(required = false) String q,
            @PageableDefault(size = 100, sort = "designation", direction = Sort.Direction.ASC)
            Pageable pageable) {

        return service.search(q, pageable);
    }
}
