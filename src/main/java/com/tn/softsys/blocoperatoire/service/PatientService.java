package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.patient.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.PatientMapper;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;
import com.tn.softsys.blocoperatoire.specification.PatientSpecification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private final PatientRepository repository;
    private final PatientMapper mapper;

    /* ================= CREATE ================= */

    public PatientResponseDTO create(PatientRequestDTO dto) {

        if (repository.existsByIdentiteFHIR(dto.getIdentiteFHIR()))
            throw new IllegalStateException("FHIR identity already exists");

        if (repository.existsByMrn(dto.getMrn()))
            throw new IllegalStateException("MRN already exists");

        Patient patient = mapper.toEntity(dto);
        return mapper.toResponse(repository.save(patient));
    }

    /* ================= UPDATE ================= */

    public PatientResponseDTO update(UUID id, PatientRequestDTO dto) {

        Patient existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        mapper.updateEntity(existing, dto);

        return mapper.toResponse(repository.save(existing));
    }

    /* ================= READ ONE ================= */

    @Transactional(readOnly = true)
    public PatientResponseDTO getById(UUID id) {

        Patient patient = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        return mapper.toResponse(patient);
    }

    /* ================= SEARCH ================= */

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> search(
            String nom,
            String prenom,
            Sexe sexe,
            GroupeSanguin groupeSanguin,
            String allergie,
            LocalDate dateNaissance,
            Pageable pageable
    ) {

        return repository.findAll(
                PatientSpecification.filter(
                        nom,
                        prenom,
                        sexe,
                        groupeSanguin,
                        allergie,
                        dateNaissance
                ),
                pageable
        ).map(mapper::toResponse);
    }

    /* ================= STATS ================= */

    @Transactional(readOnly = true)
    public long countBySexe(Sexe sexe) {
        return repository.countBySexe(sexe);
    }

    @Transactional(readOnly = true)
    public long countByGroupeSanguin(GroupeSanguin groupe) {
        return repository.countByGroupeSanguin(groupe);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getLast10Patients() {
        return repository.findTop10ByOrderByCreatedAtDesc()
                .stream()
                .map(mapper::toResponse)
                .toList();
    }

    /* ================= DELETE ================= */

    public void delete(UUID id) {

        if (!repository.existsById(id))
            throw new ResourceNotFoundException("Patient not found");

        repository.deleteById(id);
    }
}