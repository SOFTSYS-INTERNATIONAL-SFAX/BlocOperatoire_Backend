package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.mouvement.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.MouvementCorpsMapper;
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
public class MouvementCorpsService {

    private final MouvementCorpsRepository repository;
    private final CaseMortuaireRepository caseRepository;
    private final MouvementCorpsMapper mapper;

    // CREATE
    public MouvementCorpsResponseDTO create(MouvementCorpsRequestDTO dto) {

        CaseMortuaire caseMortuaire = caseRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        MouvementCorps entity = MouvementCorps.builder()
                .caseMortuaire(caseMortuaire)
                .dateMouvement(dto.getDateMouvement())
                .typeMouvement(dto.getTypeMouvement())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public MouvementCorpsResponseDTO getById(UUID id) {

        MouvementCorps entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mouvement not found"));

        return mapper.toDTO(entity);
    }

    // SEARCH + PAGINATION
    @Transactional(readOnly = true)
    public Page<MouvementCorpsResponseDTO> search(
            UUID caseId,
            LocalDateTime from,
            LocalDateTime to,
            String type,
            Pageable pageable) {

        Page<MouvementCorps> page;

        if (caseId != null) {
            page = repository.findByCaseMortuaireCaseId(caseId, pageable);

        } else if (from != null && to != null) {
            page = repository.findByDateMouvementBetween(from, to, pageable);

        } else if (type != null) {
            page = repository.findByTypeMouvement(type, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // UPDATE
    public MouvementCorpsResponseDTO update(UUID id, MouvementCorpsRequestDTO dto) {

        MouvementCorps entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mouvement not found"));

        entity.setDateMouvement(dto.getDateMouvement());
        entity.setTypeMouvement(dto.getTypeMouvement());

        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Mouvement not found");
        }

        repository.deleteById(id);
    }
}