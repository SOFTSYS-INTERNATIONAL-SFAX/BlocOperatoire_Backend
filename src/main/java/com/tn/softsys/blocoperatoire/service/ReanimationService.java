package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Reanimation;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.dto.reanimation.ReanimationRequestDTO;
import com.tn.softsys.blocoperatoire.dto.reanimation.ReanimationResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.ReanimationMapper;
import com.tn.softsys.blocoperatoire.repository.ReanimationRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ReanimationService {

    private final ReanimationRepository repository;
    private final SSPIRepository sspiRepository;
    private final ReanimationMapper mapper;

    /* ================= CREATE ================= */

    public ReanimationResponseDTO create(ReanimationRequestDTO dto) {

        if (repository.existsBySspiSspiId(dto.getSspiId())) {
            throw new IllegalStateException("Reanimation already exists for this SSPI");
        }

        SSPI sspi = sspiRepository.findById(dto.getSspiId())
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        Reanimation reanimation = Reanimation.builder()
                .sspi(sspi)
                .dateEntree(dto.getDateEntree())
                .dateSortie(dto.getDateSortie())
                .build();

        return mapper.toDTO(repository.save(reanimation));
    }

    /* ================= SEARCH ================= */

    @Transactional(readOnly = true)
    public Page<ReanimationResponseDTO> search(
            UUID sspiId,
            LocalDateTime from,
            LocalDateTime to,
            Boolean enCours,
            Pageable pageable) {

        Page<Reanimation> page;

        if (sspiId != null) {
            page = repository.findBySspiSspiId(sspiId, pageable);

        } else if (Boolean.TRUE.equals(enCours)) {
            page = repository.findByDateSortieIsNull(pageable);

        } else if (from != null && to != null) {
            page = repository.findByDateEntreeBetween(from, to, pageable);

        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    /* ================= GET BY ID ================= */

    @Transactional(readOnly = true)
    public ReanimationResponseDTO getById(UUID id) {

        Reanimation entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reanimation not found"));

        return mapper.toDTO(entity);
    }

    /* ================= UPDATE ================= */

    public ReanimationResponseDTO update(UUID id, ReanimationRequestDTO dto) {

        Reanimation entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reanimation not found"));

        entity.setDateSortie(dto.getDateSortie());

        return mapper.toDTO(repository.save(entity));
    }

    /* ================= DELETE ================= */

    public void delete(UUID id) {

        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Reanimation not found");
        }

        repository.deleteById(id);
    }
}