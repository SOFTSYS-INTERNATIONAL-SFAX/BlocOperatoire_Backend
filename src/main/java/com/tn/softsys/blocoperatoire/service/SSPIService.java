package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SSPIMapper;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SSPIService {

    private final SSPIRepository repository;
    private final InterventionRepository interventionRepository;
    private final SSPIMapper mapper;

    public SSPIResponseDTO create(SSPIRequestDTO dto) {

        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        if (repository.existsByInterventionInterventionId(dto.getInterventionId())) {
            throw new IllegalStateException("SSPI already exists");
        }

        SSPI entity = SSPI.builder()
                .intervention(intervention)
                .heureEntree(dto.getHeureEntree())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    public SSPIResponseDTO close(UUID id, LocalDateTime sortie) {

        SSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        if (sortie.isBefore(entity.getHeureEntree())) {
            throw new IllegalArgumentException("Sortie must be after entree");
        }

        entity.setHeureSortie(sortie);

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<SSPIResponseDTO> search(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Page<SSPI> page;

        if (from != null && to != null) {
            page = repository.findByHeureEntreeBetween(from, to, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("SSPI not found");
        }
        repository.deleteById(id);
    }
    @Transactional(readOnly = true)
    public SSPIResponseDTO getById(UUID id) {
        SSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));
        return mapper.toDTO(entity);
    }
}