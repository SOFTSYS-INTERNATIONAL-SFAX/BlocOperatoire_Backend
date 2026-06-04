package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Morgue;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueRequestDTO;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.MorgueMapper;
import com.tn.softsys.blocoperatoire.repository.MorgueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MorgueService {

    private static final String MODULE = "MORGUE";

    private final MorgueRepository repository;
    private final MorgueMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public MorgueResponseDTO create(MorgueRequestDTO dto) {
        Morgue entity = Morgue.builder()
                .nom(dto.getNom())
                .localisation(dto.getLocalisation())
                .build();

        Morgue saved = repository.save(entity);

        audit(
                "MORGUE_CREATE",
                saved.getMorgueId(),
                "Creation morgue nom=" + saved.getNom()
                        + " localisation=" + saved.getLocalisation()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public MorgueResponseDTO getById(UUID id) {
        Morgue entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<MorgueResponseDTO> search(String nom, Pageable pageable) {
        Page<Morgue> page;

        if (nom != null) {
            page = repository.findByNomContainingIgnoreCase(nom, pageable);
        } else {
            page = repository.findAll(pageable);
        }

        return page.map(mapper::toDTO);
    }

    public MorgueResponseDTO update(UUID id, MorgueRequestDTO dto) {
        Morgue entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        entity.setNom(dto.getNom());
        entity.setLocalisation(dto.getLocalisation());

        Morgue saved = repository.save(entity);

        audit(
                "MORGUE_UPDATE",
                saved.getMorgueId(),
                "Mise a jour morgue nom=" + saved.getNom()
                        + " localisation=" + saved.getLocalisation()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        Morgue entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        audit(
                "MORGUE_DELETE",
                entity.getMorgueId(),
                "Suppression morgue nom=" + entity.getNom()
        );

        repository.deleteById(id);
    }

    private void audit(String action, UUID referenceId, String details) {
        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                action,
                MODULE,
                referenceId,
                details,
                auditContextService.getClientIp()
        );
    }
}
