package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentRequestDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResponseDTO;
import com.tn.softsys.blocoperatoire.mapper.IncidentMapper;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;

import com.tn.softsys.blocoperatoire.repository.IncidentSSPIRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;
@Service
@RequiredArgsConstructor
@Transactional
public class IncidentService {

    private final IncidentSSPIRepository repository;
    private final SSPIRepository sspiRepository;
    private final IncidentMapper mapper;

    public IncidentResponseDTO create(IncidentRequestDTO dto) {

        SSPI sspi = sspiRepository.findById(dto.getSspiId())
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        IncidentSSPI entity = IncidentSSPI.builder()
                .sspi(sspi)
                .type(dto.getType())
                .gravite(dto.getGravite())
                .action(dto.getAction())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> findBySspi(UUID sspiId, Pageable pageable) {

        return repository.findBySspiSspiId(sspiId, pageable)
                .map(mapper::toDTO);
    }
    @Transactional(readOnly = true)
    public IncidentResponseDTO getById(UUID id) {
        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
        return mapper.toDTO(entity);
    }
    public IncidentResponseDTO update(UUID id, IncidentRequestDTO dto) {

        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        entity.setType(dto.getType());
        entity.setGravite(dto.getGravite());
        entity.setAction(dto.getAction());

        return mapper.toDTO(repository.save(entity));
    }
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Incident not found");
        }
        repository.deleteById(id);
    }
}