package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.dto.salle.SalleRequestDTO;
import com.tn.softsys.blocoperatoire.dto.salle.SalleResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SalleMapper;
import com.tn.softsys.blocoperatoire.repository.SalleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final SalleMapper mapper;

    /* =====================================================
       CREATE
       ===================================================== */

    public SalleResponseDTO create(SalleRequestDTO dto) {

        Salle salle = mapper.toEntity(dto);

        return mapper.toDTO(salleRepository.save(salle));
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    public SalleResponseDTO update(UUID id, SalleRequestDTO dto) {

        Salle existing = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        mapper.updateEntity(existing, dto);

        return mapper.toDTO(salleRepository.save(existing));
    }

    /* =====================================================
       READ ONE
       ===================================================== */

    @Transactional(readOnly = true)
    public SalleResponseDTO getById(UUID id) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        return mapper.toDTO(salle);
    }

    /* =====================================================
       SEARCH + PAGINATION
       ===================================================== */

    @Transactional(readOnly = true)
    public Page<SalleResponseDTO> search(
            String nom,
            String etageBatiment,
            Boolean active,
            Pageable pageable
    ) {

        Page<Salle> page;

        if (nom != null && etageBatiment != null) {
            page = salleRepository
                    .findByNomContainingIgnoreCaseAndEtageBatimentContainingIgnoreCase(
                            nom, etageBatiment, pageable);
        }
        else if (nom != null) {
            page = salleRepository
                    .findByNomContainingIgnoreCase(nom, pageable);
        }
        else if (etageBatiment != null) {
            page = salleRepository
                    .findByEtageBatimentContainingIgnoreCase(etageBatiment, pageable);
        }
        else if (active != null) {
            page = salleRepository
                    .findByActive(active, pageable);
        }
        else {
            page = salleRepository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    /* =====================================================
       PATCH ACTIVE (toggle formulaire)
       ===================================================== */

    public SalleResponseDTO updateActive(UUID id, Boolean active) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        salle.setActive(active);

        return mapper.toDTO(salleRepository.save(salle));
    }

    /* =====================================================
       DELETE
       ===================================================== */

    public void delete(UUID id) {

        if (!salleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salle not found");
        }

        salleRepository.deleteById(id);
    }
}