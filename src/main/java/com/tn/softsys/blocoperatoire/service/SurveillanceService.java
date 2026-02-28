package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.surveillance.SurveillanceRequestDTO;
import com.tn.softsys.blocoperatoire.dto.surveillance.SurveillanceResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;

import com.tn.softsys.blocoperatoire.mapper.SurveillanceMapper;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import com.tn.softsys.blocoperatoire.repository.SurveillanceSSPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveillanceService {

    private final SurveillanceSSPIRepository repository;
    private final SSPIRepository sspiRepository;
    private final SurveillanceMapper mapper;

    public SurveillanceResponseDTO create(SurveillanceRequestDTO dto) {

        SSPI sspi = sspiRepository.findById(dto.getSspiId())
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        SurveillanceSSPI entity = SurveillanceSSPI.builder()
                .sspi(sspi)
                .tensionArterielle(dto.getTensionArterielle())
                .frequenceCardiaque(dto.getFrequenceCardiaque())
                .spo2(dto.getSpo2())
                .temperature(dto.getTemperature())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<SurveillanceResponseDTO> findBySspi(UUID sspiId, Pageable pageable) {

        return repository.findBySspiSspiId(sspiId, pageable)
                .map(mapper::toDTO);
    }
    @Transactional(readOnly = true)
    public SurveillanceResponseDTO getById(UUID id) {
        SurveillanceSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surveillance not found"));
        return mapper.toDTO(entity);
    }public SurveillanceResponseDTO update(UUID id, SurveillanceRequestDTO dto) {

        SurveillanceSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surveillance not found"));

        entity.setTensionArterielle(dto.getTensionArterielle());
        entity.setFrequenceCardiaque(dto.getFrequenceCardiaque());
        entity.setSpo2(dto.getSpo2());
        entity.setTemperature(dto.getTemperature());

        return mapper.toDTO(repository.save(entity));
    }
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Surveillance not found");
        }
        repository.deleteById(id);
    }
}