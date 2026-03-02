package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.intervention.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.InterventionMapper;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InterventionService {

    private final InterventionRepository interventionRepository;
    private final PatientRepository patientRepository;
    private final SalleRepository salleRepository;
    private final PlanningBlocRepository planningBlocRepository;
    private final InterventionMapper mapper;

    /* CREATE */

    public InterventionResponseDTO create(InterventionRequestDTO dto) {

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Patient not found"));

        Salle salle = null;
        if (dto.getSalleId() != null) {
            salle = salleRepository.findById(dto.getSalleId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Salle not found"));
        }

        PlanningBloc planning = null;
        if (dto.getPlanningId() != null) {
            planning = planningBlocRepository.findById(dto.getPlanningId())
                    .orElseThrow(() ->
                            new ResourceNotFoundException("Planning not found"));
        }

        Intervention intervention = mapper.toEntity(dto);

        intervention.setPatient(patient);
        intervention.setSalle(salle);
        intervention.setPlanningBloc(planning);

        return mapper.toDTO(interventionRepository.save(intervention));
    }

    /* UPDATE */

    public InterventionResponseDTO update(UUID id, InterventionRequestDTO dto) {

        Intervention existing = interventionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Intervention not found"));

        mapper.updateEntity(existing, dto);

        return mapper.toDTO(interventionRepository.save(existing));
    }

    /* GET ONE */

    @Transactional(readOnly = true)
    public InterventionResponseDTO getById(UUID id) {

        Intervention intervention = interventionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Intervention not found"));

        return mapper.toDTO(intervention);
    }

    /* SEARCH + PAGINATION */

    @Transactional(readOnly = true)
    public Page<InterventionResponseDTO> search(
            UUID patientId,
            String statut,
            Boolean urgence,
            String codeActe,
            Pageable pageable) {

        Page<Intervention> page;

        if (patientId != null) {
            page = interventionRepository.findByPatientPatientId(patientId, pageable);
        }
        else if (statut != null) {
            page = interventionRepository.findByStatutContainingIgnoreCase(statut, pageable);
        }
        else if (Boolean.TRUE.equals(urgence)) {
            page = interventionRepository.findByUrgenceOMSTrue(pageable);
        }
        else if (codeActe != null) {
            page = interventionRepository.findByCodeActeContainingIgnoreCase(codeActe, pageable);
        }
        else {
            page = interventionRepository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    /* DELETE */

    public void delete(UUID id) {
        if (!interventionRepository.existsById(id)) {
            throw new ResourceNotFoundException("Intervention not found");
        }
        interventionRepository.deleteById(id);
    }
}