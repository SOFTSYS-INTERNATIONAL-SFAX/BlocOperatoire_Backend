package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.PlanningBloc;
import com.tn.softsys.blocoperatoire.dto.planning.PlanningBlocRequestDTO;
import com.tn.softsys.blocoperatoire.dto.planning.PlanningBlocResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.PlanningBlocMapper;
import com.tn.softsys.blocoperatoire.repository.PlanningBlocRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PlanningBlocService {

    private final PlanningBlocRepository repository;
    private final PlanningBlocMapper mapper;

    // CREATE
    public PlanningBlocResponseDTO create(PlanningBlocRequestDTO dto) {

        PlanningBloc entity = PlanningBloc.builder()
                .date(dto.getDate())
                .build();

        return mapper.toDTO(repository.save(entity));
    }

    // GET BY ID
    @Transactional(readOnly = true)
    public PlanningBlocResponseDTO getById(UUID id) {

        PlanningBloc entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanningBloc not found"));

        return mapper.toDTO(entity);
    }

    // SEARCH + PAGINATION
    @Transactional(readOnly = true)
    public Page<PlanningBlocResponseDTO> search(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable) {

        Page<PlanningBloc> page;

        if (from != null && to != null) {
            page = repository.findByDateBetween(from, to, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    // UPDATE
    public PlanningBlocResponseDTO update(UUID id, PlanningBlocRequestDTO dto) {

        PlanningBloc entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanningBloc not found"));

        entity.setDate(dto.getDate());

        return mapper.toDTO(repository.save(entity));
    }

    // DELETE
    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("PlanningBloc not found");
        }

        repository.deleteById(id);
    }
}