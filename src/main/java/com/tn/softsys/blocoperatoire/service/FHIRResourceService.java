package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.FHIRResource;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.dto.fhir.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.FHIRResourceMapper;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FHIRResourceService {

    private final FHIRResourceRepository repository;
    private final PatientRepository patientRepository;
    private final InterventionRepository interventionRepository;
    private final FHIRResourceMapper mapper;

    public FHIRResourceResponseDTO create(FHIRResourceRequestDTO dto) {

        Patient patient = null;
        Intervention intervention = null;

        if (dto.getPatientId() != null) {
            patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        }

        if (dto.getInterventionId() != null) {
            intervention = interventionRepository.findById(dto.getInterventionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));
        }

        FHIRResource entity = FHIRResource.builder()
                .resourceType(dto.getResourceType())
                .payloadJson(dto.getPayloadJson())
                .patient(patient)
                .intervention(intervention)
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public Page<FHIRResourceResponseDTO> search(
            UUID patientId,
            UUID interventionId,
            Pageable pageable) {

        Page<FHIRResource> page;

        if (patientId != null) {
            page = repository.findByPatientPatientId(patientId, pageable);

        } else if (interventionId != null) {
            page = repository.findByInterventionInterventionId(interventionId, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }
}