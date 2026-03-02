package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.dto.salle.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SalleMapper;
import com.tn.softsys.blocoperatoire.repository.SalleRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SalleService {

    private final SalleRepository salleRepository;
    private final SalleMapper mapper;

    /* CREATE */

    public SalleResponseDTO create(SalleRequestDTO dto) {
        Salle salle = mapper.toEntity(dto);
        return mapper.toDTO(salleRepository.save(salle));
    }

    /* UPDATE */

    public SalleResponseDTO update(UUID id, SalleRequestDTO dto) {

        Salle existing = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        mapper.updateEntity(existing, dto);

        return mapper.toDTO(salleRepository.save(existing));
    }

    /* GET ONE */

    @Transactional(readOnly = true)
    public SalleResponseDTO getById(UUID id) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        return mapper.toDTO(salle);
    }

    /* SEARCH + PAGINATION */

    @Transactional(readOnly = true)
    public Page<SalleResponseDTO> search(
            String nom,
            String type,
            Boolean disponible,
            Pageable pageable
    ) {

        Page<Salle> page;

        if (nom != null && type != null) {
            page = salleRepository
                    .findByNomContainingIgnoreCaseAndTypeContainingIgnoreCase(
                            nom, type, pageable);
        }
        else if (nom != null) {
            page = salleRepository
                    .findByNomContainingIgnoreCase(nom, pageable);
        }
        else if (type != null) {
            page = salleRepository
                    .findByTypeContainingIgnoreCase(type, pageable);
        }
        else if (disponible != null) {
            page = salleRepository
                    .findByDisponible(disponible, pageable);
        }
        else {
            page = salleRepository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    /* PATCH DISPONIBILITE */

    public SalleResponseDTO updateDisponibilite(UUID id, Boolean disponible) {

        Salle salle = salleRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Salle not found"));

        salle.setDisponible(disponible);

        return mapper.toDTO(salleRepository.save(salle));
    }

    /* DELETE */

    public void delete(UUID id) {

        if (!salleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Salle not found");
        }

        salleRepository.deleteById(id);
    }
}