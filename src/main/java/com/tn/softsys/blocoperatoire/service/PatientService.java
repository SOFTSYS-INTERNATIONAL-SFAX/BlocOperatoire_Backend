package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.PatientMapper;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository patientRepository;
    private final PatientMapper patientMapper;

    /* =====================================================
       CREATE
       ===================================================== */

    public PatientResponseDTO create(PatientRequestDTO dto) {

        if (patientRepository.existsByIdentiteFHIR(dto.getIdentiteFHIR())) {
            throw new IllegalStateException(
                    "Patient with same FHIR identity already exists"
            );
        }

        Patient patient = patientMapper.toEntity(dto);
        Patient saved = patientRepository.save(patient);

        return patientMapper.toResponse(saved);
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    public PatientResponseDTO update(UUID id, PatientRequestDTO dto) {

        Patient existing = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + id
                        )
                );

        // Vérifier unicité FHIR si modifiée
        if (!existing.getIdentiteFHIR().equals(dto.getIdentiteFHIR())
                && patientRepository.existsByIdentiteFHIR(dto.getIdentiteFHIR())) {

            throw new IllegalStateException(
                    "Another patient already has this FHIR identity"
            );
        }

        patientMapper.updateEntity(existing, dto);

        Patient updated = patientRepository.save(existing);

        return patientMapper.toResponse(updated);
    }

    /* =====================================================
       READ ONE
       ===================================================== */

    @Transactional(readOnly = true)
    public PatientResponseDTO getById(UUID id) {

        Patient patient = patientRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Patient not found with id: " + id
                        )
                );

        return patientMapper.toResponse(patient);
    }

    /* =====================================================
       SEARCH + PAGINATION
       ===================================================== */

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> search(
            String nom,
            String prenom,
            Pageable pageable) {

        Page<Patient> page;

        if (nom != null && prenom != null) {
            page = patientRepository
                    .findByNomContainingIgnoreCaseAndPrenomContainingIgnoreCase(
                            nom, prenom, pageable
                    );
        }
        else if (nom != null) {
            page = patientRepository
                    .findByNomContainingIgnoreCase(nom, pageable);
        }
        else if (prenom != null) {
            page = patientRepository
                    .findByPrenomContainingIgnoreCase(prenom, pageable);
        }
        else {
            page = patientRepository.findAll(pageable);
        }

        return page.map(patientMapper::toResponse);
    }

    /* =====================================================
       DELETE
       ===================================================== */

    public void delete(UUID id) {

        if (!patientRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Patient not found with id: " + id
            );
        }

        patientRepository.deleteById(id);
    }
}