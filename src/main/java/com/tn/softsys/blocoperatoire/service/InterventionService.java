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

    private final InterventionRepository repository;
    private final PatientRepository patientRepository;
    private final SalleRepository salleRepository;
    private final PlanningBlocRepository planningRepository;
    private final UserRepository userRepository;

    private final InterventionMapper mapper;

    /* =====================================================
       CREATE
       ===================================================== */

    public InterventionResponseDTO create(InterventionRequestDTO dto) {

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Salle salle = dto.getSalleId() != null ?
                salleRepository.findById(dto.getSalleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Salle not found"))
                : null;

        PlanningBloc planning = dto.getPlanningId() != null ?
                planningRepository.findById(dto.getPlanningId())
                        .orElseThrow(() -> new ResourceNotFoundException("Planning not found"))
                : null;

        User chirurgien = dto.getChirurgienId() != null ?
                userRepository.findById(dto.getChirurgienId())
                        .orElseThrow(() -> new ResourceNotFoundException("Chirurgien not found"))
                : null;

        User anesthesiste = dto.getAnesthesisteId() != null ?
                userRepository.findById(dto.getAnesthesisteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Anesthesiste not found"))
                : null;

        Intervention intervention = mapper.toEntity(
                dto, patient, salle, planning, chirurgien, anesthesiste
        );

        return mapper.toResponse(repository.save(intervention));
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    public InterventionResponseDTO update(UUID id, InterventionRequestDTO dto) {

        Intervention existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Salle salle = dto.getSalleId() != null ?
                salleRepository.findById(dto.getSalleId())
                        .orElseThrow(() -> new ResourceNotFoundException("Salle not found"))
                : null;

        PlanningBloc planning = dto.getPlanningId() != null ?
                planningRepository.findById(dto.getPlanningId())
                        .orElseThrow(() -> new ResourceNotFoundException("Planning not found"))
                : null;

        User chirurgien = dto.getChirurgienId() != null ?
                userRepository.findById(dto.getChirurgienId())
                        .orElseThrow(() -> new ResourceNotFoundException("Chirurgien not found"))
                : null;

        User anesthesiste = dto.getAnesthesisteId() != null ?
                userRepository.findById(dto.getAnesthesisteId())
                        .orElseThrow(() -> new ResourceNotFoundException("Anesthesiste not found"))
                : null;

        mapper.updateEntity(
                existing, dto, patient, salle, planning, chirurgien, anesthesiste
        );

        return mapper.toResponse(repository.save(existing));
    }

    /* =====================================================
       READ ONE
       ===================================================== */

    @Transactional(readOnly = true)
    public InterventionResponseDTO getById(UUID id) {

        Intervention intervention = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        return mapper.toResponse(intervention);
    }

    /* =====================================================
       SEARCH
       ===================================================== */

    public Page<InterventionResponseDTO> search(
            UUID patientId,
            StatutIntervention statut,
            Boolean urgenceOMS,
            String codeActe,
            Pageable pageable
    ) {

        return repository.findAll((root, query, cb) -> {

            var predicate = cb.conjunction();

            if (patientId != null)
                predicate = cb.and(predicate,
                        cb.equal(root.get("patient").get("patientId"), patientId));

            if (statut != null)
                predicate = cb.and(predicate,
                        cb.equal(root.get("statut"), statut));

            if (urgenceOMS != null)
                predicate = cb.and(predicate,
                        cb.equal(root.get("urgenceOMS"), urgenceOMS));

            if (codeActe != null)
                predicate = cb.and(predicate,
                        cb.like(cb.lower(root.get("codeActe")),
                                "%" + codeActe.toLowerCase() + "%"));

            return predicate;

        }, pageable).map(mapper::toResponse);
    }
    /* =====================================================
       DELETE
       ===================================================== */

    public void delete(UUID id) {

        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Intervention not found");

        repository.deleteById(id);
    }
}