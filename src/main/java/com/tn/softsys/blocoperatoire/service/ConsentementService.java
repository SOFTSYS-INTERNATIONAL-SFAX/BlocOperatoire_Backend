package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementRequestDTO;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.ConsentementMapper;
import com.tn.softsys.blocoperatoire.repository.ConsentementRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ConsentementService {

    private final ConsentementRepository repository;
    private final PatientRepository patientRepository;
    private final InterventionRepository interventionRepository;
    private final ConsentementMapper mapper;

    // =========================================================
    // CREATE
    // =========================================================

    public ConsentementResponseDTO create(ConsentementRequestDTO dto) {

        // 🔎 Vérifier existence patient
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // 🔎 Vérifier existence intervention
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        // 🔐 Vérification cohérence métier : intervention appartient au patient
        if (!intervention.getPatient().getPatientId()
                .equals(patient.getPatientId())) {

            throw new IllegalStateException(
                    "Intervention does not belong to the given patient"
            );
        }

        // 🔐 Empêcher doublon
        boolean alreadyExists =
                repository.existsByPatientPatientIdAndInterventionInterventionIdAndType(
                        dto.getPatientId(),
                        dto.getInterventionId(),
                        dto.getType()
                );

        if (alreadyExists) {
            throw new IllegalStateException(
                    "Consentement already exists for this patient and intervention"
            );
        }

        Consentement entity = Consentement.builder()
                .patient(patient)
                .intervention(intervention)
                .type(dto.getType())
                .date(dto.getDate())
                .valide(dto.getValide())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // =========================================================
    // GET BY ID
    // =========================================================

    @Transactional(readOnly = true)
    public ConsentementResponseDTO getById(UUID id) {

        Consentement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consentement not found"));

        return mapper.toDTO(entity);
    }

    // =========================================================
    // SEARCH + PAGINATION
    // =========================================================

    @Transactional(readOnly = true)
    public Page<ConsentementResponseDTO> search(
            UUID patientId,
            UUID interventionId,
            Pageable pageable) {

        Page<Consentement> page;

        if (patientId != null) {
            page = repository.findByPatientPatientId(patientId, pageable);

        } else if (interventionId != null) {
            page = repository.findByInterventionInterventionId(interventionId, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // =========================================================
    // UPDATE
    // =========================================================

    public ConsentementResponseDTO update(UUID id, ConsentementRequestDTO dto) {

        Consentement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consentement not found"));

        // ⚠️ On ne modifie PAS patient ni intervention
        entity.setType(dto.getType());
        entity.setDate(dto.getDate());
        entity.setValide(dto.getValide());

        return mapper.toDTO(repository.save(entity));
    }

    // =========================================================
    // DELETE
    // =========================================================

    public void delete(UUID id) {

        Consentement entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consentement not found"));

        repository.delete(entity);
    }
}
