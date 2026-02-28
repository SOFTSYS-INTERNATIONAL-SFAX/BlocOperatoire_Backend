package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.dto.deces.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.DecesMapper;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class DecesService {

    private final DecesRepository repository;
    private final InterventionRepository interventionRepository;
    private final DecesMapper mapper;

    // ================= CREATE =================

    public DecesResponseDTO create(DecesRequestDTO dto) {

        if (repository.existsByInterventionInterventionId(dto.getInterventionId())) {
            throw new IllegalStateException(
                    "Deces already declared for this intervention");
        }

        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Intervention not found"));

        Deces entity = Deces.builder()
                .intervention(intervention)
                .dateDeces(dto.getDateDeces())
                .cause(dto.getCause())
                .constatPar(dto.getConstatPar())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public DecesResponseDTO getById(UUID id) {

        Deces entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deces not found"));

        return mapper.toDTO(entity);
    }

    // ================= SEARCH + PAGINATION =================

    @Transactional(readOnly = true)
    public Page<DecesResponseDTO> search(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Page<Deces> page;

        if (from != null && to != null) {
            page = repository.findByDateDecesBetween(from, to, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // ================= UPDATE =================

    public DecesResponseDTO update(UUID id, DecesRequestDTO dto) {

        Deces entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Deces not found"));

        entity.setDateDeces(dto.getDateDeces());
        entity.setCause(dto.getCause());
        entity.setConstatPar(dto.getConstatPar());

        return mapper.toDTO(repository.save(entity));
    }

    // ================= DELETE =================

    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Deces not found");
        }

        repository.deleteById(id);
    }
}