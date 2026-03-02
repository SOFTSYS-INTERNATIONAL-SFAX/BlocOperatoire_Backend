package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Morgue;
import com.tn.softsys.blocoperatoire.dto.morgue.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.MorgueMapper;
import com.tn.softsys.blocoperatoire.repository.MorgueRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MorgueService {

    private final MorgueRepository repository;
    private final MorgueMapper mapper;

    // CREATE
    public MorgueResponseDTO create(MorgueRequestDTO dto) {

        Morgue entity = Morgue.builder()
                .nom(dto.getNom())
                .localisation(dto.getLocalisation())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public MorgueResponseDTO getById(UUID id) {

        Morgue entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        return mapper.toDTO(entity);
    }

    // SEARCH + PAGINATION
    @Transactional(readOnly = true)
    public Page<MorgueResponseDTO> search(String nom, Pageable pageable) {

        Page<Morgue> page;

        if (nom != null) {
            page = repository.findByNomContainingIgnoreCase(nom, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // UPDATE
    public MorgueResponseDTO update(UUID id, MorgueRequestDTO dto) {

        Morgue entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        entity.setNom(dto.getNom());
        entity.setLocalisation(dto.getLocalisation());

        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Morgue not found");
        }

        repository.deleteById(id);
    }
}