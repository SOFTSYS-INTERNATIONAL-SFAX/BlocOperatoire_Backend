package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.casemor.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.CaseMortuaireMapper;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CaseMortuaireService {

    private final CaseMortuaireRepository repository;
    private final MorgueRepository morgueRepository;
    private final DecesRepository decesRepository;
    private final CaseMortuaireMapper mapper;

    // CREATE
    public CaseMortuaireResponseDTO create(CaseMortuaireRequestDTO dto) {

        if (repository.existsByNumeroCase(dto.getNumeroCase())) {
            throw new IllegalStateException("Numero case already exists");
        }

        Morgue morgue = morgueRepository.findById(dto.getMorgueId())
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        CaseMortuaire entity = CaseMortuaire.builder()
                .numeroCase(dto.getNumeroCase())
                .occupee(false)
                .morgue(morgue)
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public CaseMortuaireResponseDTO getById(UUID id) {

        CaseMortuaire entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        return mapper.toDTO(entity);
    }

    // SEARCH + PAGINATION
    @Transactional(readOnly = true)
    public Page<CaseMortuaireResponseDTO> search(
            UUID morgueId,
            Boolean occupee,
            Pageable pageable) {

        Page<CaseMortuaire> page;

        if (morgueId != null) {
            page = repository.findByMorgueMorgueId(morgueId, pageable);

        } else if (occupee != null) {
            page = repository.findByOccupee(occupee, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // UPDATE
    public CaseMortuaireResponseDTO update(UUID id, CaseMortuaireRequestDTO dto) {

        CaseMortuaire entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        entity.setNumeroCase(dto.getNumeroCase());

        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Case not found");
        }

        repository.deleteById(id);
    }

    // AFFECTER
    public CaseMortuaireResponseDTO affecter(UUID caseId, UUID decesId) {

        CaseMortuaire caseMortuaire = repository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (caseMortuaire.getOccupee()) {
            throw new IllegalStateException("Case already occupied");
        }

        Deces deces = decesRepository.findById(decesId)
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        caseMortuaire.setDeces(deces);
        caseMortuaire.setOccupee(true);

        return mapper.toDTO(repository.save(caseMortuaire));
    }

    // LIBERER
    public CaseMortuaireResponseDTO liberer(UUID caseId) {

        CaseMortuaire caseMortuaire = repository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        caseMortuaire.setDeces(null);
        caseMortuaire.setOccupee(false);

        return mapper.toDTO(repository.save(caseMortuaire));
    }
}