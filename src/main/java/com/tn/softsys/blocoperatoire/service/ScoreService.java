package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.score.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.factory.ScoreFactory;
import com.tn.softsys.blocoperatoire.mapper.ScoreMapper;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoreService {

    private final ScoreRepository scoreRepository;
    private final InterventionRepository interventionRepository;
    private final ScoreMapper mapper;

    public ScoreResponseDTO create(ScoreRequestDTO dto) {

        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        Score score = ScoreFactory.create(dto.getType());

        score.setValeur(dto.getValeur());
        score.setIntervention(intervention);
        score.calculerEtat();

        return mapper.toDTO(scoreRepository.save(score));
    }

    @Transactional(readOnly = true)
    public ScoreResponseDTO getById(UUID id) {

        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));

        return mapper.toDTO(score);
    }

    @Transactional(readOnly = true)
    public Page<ScoreResponseDTO> search(UUID interventionId, Pageable pageable) {

        Page<Score> page;

        if (interventionId != null) {
            page = scoreRepository
                    .findByInterventionInterventionId(interventionId, pageable);
        } else {
            page = scoreRepository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    public void delete(UUID id) {

        if (!scoreRepository.existsById(id)) {
            throw new ResourceNotFoundException("Score not found");
        }

        scoreRepository.deleteById(id);
    }

    public ScoreResponseDTO update(UUID id, ScoreRequestDTO dto) {

        Score score = scoreRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Score not found"));

        // Optionnel : empêcher changement de type
        ScoreType existingType = mapper.resolveType(score);
        if (!existingType.equals(dto.getType())) {
            throw new IllegalStateException("Score type cannot be changed");
        }

        // Optionnel : empêcher changement d'intervention
        if (!score.getIntervention().getInterventionId()
                .equals(dto.getInterventionId())) {
            throw new IllegalStateException("Intervention cannot be changed");
        }

        score.setValeur(dto.getValeur());

        // etat sera recalculé via @PreUpdate
        Score updated = scoreRepository.save(score);

        return mapper.toDTO(updated);
    }
}