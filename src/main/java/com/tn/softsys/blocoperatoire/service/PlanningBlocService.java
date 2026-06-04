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

    private static final String MODULE = "PLANIFICATION_BLOC";

    private final PlanningBlocRepository repository;
    private final PlanningBlocMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public PlanningBlocResponseDTO create(PlanningBlocRequestDTO dto) {
        PlanningBloc entity = PlanningBloc.builder()
                .date(dto.getDate())
                .build();

        PlanningBloc saved = repository.save(entity);

        audit(
                "PLANNING_BLOC_CREATE",
                saved.getPlanningId(),
                "Creation planning bloc date=" + saved.getDate()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public PlanningBlocResponseDTO getById(UUID id) {
        PlanningBloc entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanningBloc not found"));

        return mapper.toDTO(entity);
    }

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

    public PlanningBlocResponseDTO update(UUID id, PlanningBlocRequestDTO dto) {
        PlanningBloc entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanningBloc not found"));

        entity.setDate(dto.getDate());

        PlanningBloc saved = repository.save(entity);

        audit(
                "PLANNING_BLOC_UPDATE",
                saved.getPlanningId(),
                "Mise a jour planning bloc date=" + saved.getDate()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        PlanningBloc entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("PlanningBloc not found"));

        audit(
                "PLANNING_BLOC_DELETE",
                entity.getPlanningId(),
                "Suppression planning bloc date=" + entity.getDate()
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
