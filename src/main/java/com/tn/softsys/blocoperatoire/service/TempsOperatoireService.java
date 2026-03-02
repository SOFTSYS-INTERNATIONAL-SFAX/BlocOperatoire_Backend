package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.tempsoperatoire.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.TempsOperatoireMapper;
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
public class TempsOperatoireService {

    private final TempsOperatoireRepository repository;
    private final InterventionRepository interventionRepository;
    private final TempsOperatoireMapper mapper;

    // ================= CREATE =================

    public TempsOperatoireResponseDTO create(TempsOperatoireRequestDTO dto) {

        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() ->
                        new ResourceNotFoundException("Intervention not found"));

        if (repository.existsByInterventionInterventionId(dto.getInterventionId())) {
            throw new IllegalStateException(
                    "TempsOperatoire already exists for this intervention");
        }

        validateChronology(dto);

        TempsOperatoire entity = TempsOperatoire.builder()
                .intervention(intervention)
                .entreeBloc(dto.getEntreeBloc())
                .debutAnesthesie(dto.getDebutAnesthesie())
                .incision(dto.getIncision())
                .finActe(dto.getFinActe())
                .sortieSalle(dto.getSortieSalle())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // ================= GET BY ID =================

    @Transactional(readOnly = true)
    public TempsOperatoireResponseDTO getById(UUID id) {

        TempsOperatoire entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("TempsOperatoire not found"));

        return mapper.toDTO(entity);
    }

    // ================= GET BY INTERVENTION =================

    @Transactional(readOnly = true)
    public TempsOperatoireResponseDTO getByIntervention(UUID interventionId) {

        TempsOperatoire entity =
                repository.findByInterventionInterventionId(interventionId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException("TempsOperatoire not found"));

        return mapper.toDTO(entity);
    }

    // ================= SEARCH + PAGINATION =================

    @Transactional(readOnly = true)
    public Page<TempsOperatoireResponseDTO> search(
            UUID interventionId,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Page<TempsOperatoire> page;

        if (interventionId != null) {
            page = repository.findByInterventionInterventionId(
                    interventionId, pageable);

        } else if (from != null && to != null) {
            page = repository.findByEntreeBlocBetween(
                    from, to, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // ================= UPDATE =================

    public TempsOperatoireResponseDTO update(UUID id, TempsOperatoireRequestDTO dto) {

        TempsOperatoire entity = repository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("TempsOperatoire not found"));

        // Ne jamais changer l'intervention
        if (!entity.getIntervention().getInterventionId()
                .equals(dto.getInterventionId())) {

            throw new IllegalArgumentException(
                    "Intervention cannot be changed");
        }

        validateChronology(dto);

        entity.setEntreeBloc(dto.getEntreeBloc());
        entity.setDebutAnesthesie(dto.getDebutAnesthesie());
        entity.setIncision(dto.getIncision());
        entity.setFinActe(dto.getFinActe());
        entity.setSortieSalle(dto.getSortieSalle());

        return mapper.toDTO(repository.save(entity));
    }

    // ================= DELETE =================

    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("TempsOperatoire not found");
        }

        repository.deleteById(id);
    }

    // ================= VALIDATION MÉTIER =================

    private void validateChronology(TempsOperatoireRequestDTO dto) {

        if (dto.getEntreeBloc() != null &&
                dto.getDebutAnesthesie() != null &&
                dto.getDebutAnesthesie().isBefore(dto.getEntreeBloc())) {

            throw new IllegalArgumentException(
                    "Debut anesthesie must be after entree bloc");
        }

        if (dto.getDebutAnesthesie() != null &&
                dto.getIncision() != null &&
                dto.getIncision().isBefore(dto.getDebutAnesthesie())) {

            throw new IllegalArgumentException(
                    "Incision must be after debut anesthesie");
        }

        if (dto.getIncision() != null &&
                dto.getFinActe() != null &&
                dto.getFinActe().isBefore(dto.getIncision())) {

            throw new IllegalArgumentException(
                    "Fin acte must be after incision");
        }
    }
}