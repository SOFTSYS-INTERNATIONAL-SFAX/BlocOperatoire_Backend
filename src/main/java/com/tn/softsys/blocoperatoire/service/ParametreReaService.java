package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.ParametreRea;
import com.tn.softsys.blocoperatoire.domain.Reanimation;
import com.tn.softsys.blocoperatoire.dto.parametrerea.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.ParametreReaMapper;
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
public class ParametreReaService {

    private final ParametreReaRepository repository;
    private final ReanimationRepository reanimationRepository;
    private final ParametreReaMapper mapper;

    /* ================= CREATE ================= */

    public ParametreReaResponseDTO create(ParametreReaRequestDTO dto) {

        Reanimation reanimation = reanimationRepository.findById(dto.getReaId())
                .orElseThrow(() -> new ResourceNotFoundException("Reanimation not found"));

        ParametreRea entity = ParametreRea.builder()
                .reanimation(reanimation)
                .dateMesure(dto.getDateMesure())
                .ventilation(dto.getVentilation())
                .vasopresseurActif(dto.getVasopresseurActif())
                .diurese(dto.getDiurese())
                .build();

        repository.save(entity);

        return mapper.toDTO(entity);
    }

    /* ================= GET BY ID ================= */

    @Transactional(readOnly = true)
    public ParametreReaResponseDTO getById(UUID id) {

        ParametreRea entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParametreRea not found"));

        return mapper.toDTO(entity);
    }

    /* ================= SEARCH ================= */

    @Transactional(readOnly = true)
    public Page<ParametreReaResponseDTO> search(
            UUID reaId,
            Boolean ventilation,
            Boolean vasopresseurActif,
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Page<ParametreRea> page;

        if (reaId != null) {
            page = repository.findByReanimationReaIdOrderByDateMesureAsc(reaId, pageable);

        } else if (ventilation != null) {
            page = repository.findByVentilation(ventilation, pageable);

        } else if (vasopresseurActif != null) {
            page = repository.findByVasopresseurActif(vasopresseurActif, pageable);

        } else if (from != null && to != null) {
            page = repository.findByDateMesureBetween(from, to, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    /* ================= UPDATE ================= */

    public ParametreReaResponseDTO update(UUID id, ParametreReaRequestDTO dto) {

        ParametreRea entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ParametreRea not found"));

        entity.setDateMesure(dto.getDateMesure());
        entity.setVentilation(dto.getVentilation());
        entity.setVasopresseurActif(dto.getVasopresseurActif());
        entity.setDiurese(dto.getDiurese());

        return mapper.toDTO(repository.save(entity));
    }

    /* ================= DELETE ================= */

    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("ParametreRea not found");
        }

        repository.deleteById(id);
    }
}