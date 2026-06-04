package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIClotureRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SSPIMapper;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SSPIService {

    private static final String MODULE = "SSPI";

    private final SSPIRepository repository;
    private final InterventionRepository interventionRepository;
    private final SSPIMapper mapper;
    private final AlertService alertService;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public SSPIResponseDTO create(SSPIRequestDTO dto) {
        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        if (repository.existsByInterventionInterventionId(dto.getInterventionId())) {
            throw new IllegalStateException("SSPI already exists");
        }

        SSPI entity = SSPI.builder()
                .intervention(intervention)
                .heureEntree(dto.getHeureEntree() != null ? dto.getHeureEntree() : LocalDateTime.now())
                .posteCode(normalize(dto.getPosteCode()))
                .build();

        SSPI saved = repository.save(entity);

        audit(
                "SSPI_CREATE",
                saved.getSspiId(),
                "Creation SSPI intervention=" + saved.getIntervention().getInterventionId()
                        + " heureEntree=" + saved.getHeureEntree()
                        + " poste=" + saved.getPosteCode()
        );

        return mapper.toDTO(saved);
    }

    public SSPIResponseDTO close(UUID id, SSPIClotureRequestDTO dto) {
        SSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        if (entity.getHeureEntree() == null) {
            throw new IllegalStateException("Heure entree SSPI manquante");
        }

        LocalDateTime sortie = dto.getHeureSortie() != null ? dto.getHeureSortie() : LocalDateTime.now();

        if (sortie.isBefore(entity.getHeureEntree())) {
            throw new IllegalArgumentException("Sortie must be after entree");
        }

        User currentUser = getCurrentUserStrict();

        entity.setHeureSortie(sortie);
        entity.setDestinationSortie(normalize(dto.getDestinationSortie()));
        entity.setMotifSortie(normalize(dto.getMotifSortie()));
        entity.setAldreteSortie(dto.getAldreteSortie());
        entity.setDecisionMedicale(normalize(dto.getDecisionMedicale()));
        entity.setObservationsSortie(normalize(dto.getObservationsSortie()));
        entity.setTransmissionResume(normalize(dto.getTransmissionResume()));
        entity.setSortieValideePar(currentUser);

        if (entity.getIntervention() != null) {
            entity.getIntervention().setStatut(StatutIntervention.CLOTUREE);
        }

        SSPI saved = repository.save(entity);

        if (saved.getIntervention() != null && saved.getIntervention().getInterventionId() != null) {
            alertService.resolveSspiOverrunAlerts(saved.getIntervention().getInterventionId());
        }

        audit(
                "SSPI_CLOSE",
                saved.getSspiId(),
                "Cloture SSPI intervention=" + saved.getIntervention().getInterventionId()
                        + " heureSortie=" + saved.getHeureSortie()
                        + " destination=" + saved.getDestinationSortie()
                        + " motif=" + saved.getMotifSortie()
                        + " aldreteSortie=" + saved.getAldreteSortie()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<SSPIResponseDTO> search(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        Page<SSPI> page = (from != null && to != null)
                ? repository.findByHeureEntreeBetween(from, to, pageable)
                : repository.findAll(pageable);

        return page.map(mapper::toDTO);
    }

    public void delete(UUID id) {
        SSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        audit(
                "SSPI_DELETE",
                entity.getSspiId(),
                "Suppression SSPI intervention=" + (entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null)
        );

        repository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public SSPIResponseDTO getById(UUID id) {
        SSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));
        return mapper.toDTO(entity);
    }

    private String normalize(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private User getCurrentUserStrict() {
        User user = auditContextService.getCurrentUserOrNull();
        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }
        return user;
    }

    private void audit(String action, UUID referenceId, String details) {
        auditLogService.log(
                getCurrentUserStrict(),
                action,
                MODULE,
                referenceId,
                details,
                auditContextService.getClientIp()
        );
    }
}
