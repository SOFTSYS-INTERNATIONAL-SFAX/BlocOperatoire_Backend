package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.InterventionCatalog;
import com.tn.softsys.blocoperatoire.dto.interventioncatalog.InterventionCatalogResponseDTO;
import com.tn.softsys.blocoperatoire.repository.InterventionCatalogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterventionCatalogService {

    private final InterventionCatalogRepository repository;

    public Page<InterventionCatalogResponseDTO> search(String q, Pageable pageable) {
        Page<InterventionCatalog> page;

        if (q == null || q.isBlank()) {
            page = repository.findByActiveTrueOrderByDesignationAsc(pageable);
        } else {
            page = repository.searchActiveCatalog(q.trim(), pageable);
        }

        return page.map(this::toDTO);
    }

    private InterventionCatalogResponseDTO toDTO(InterventionCatalog item) {
        return InterventionCatalogResponseDTO.builder()
                .catalogId(item.getCatalogId())
                .designation(item.getDesignation())
                .designationEn(item.getDesignationEn())
                .designationAr(item.getDesignationAr())
                .cotationUnite(item.getCotationUnite())
                .cotationValeur(item.getCotationValeur())
                .dureeMinutes(item.getDureeMinutes())
                .build();
    }
}
