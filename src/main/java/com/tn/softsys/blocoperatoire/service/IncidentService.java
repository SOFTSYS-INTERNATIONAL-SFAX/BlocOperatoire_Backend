package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentCreateRequestDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResolveRequestDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentResponseDTO;
import com.tn.softsys.blocoperatoire.dto.incident.IncidentUpdateRequestDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.IncidentMapper;
import com.tn.softsys.blocoperatoire.repository.IncidentSSPIRepository;
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
public class IncidentService {

    private static final String MODULE = "SSPI_INCIDENT";

    private final IncidentSSPIRepository repository;
    private final SSPIRepository sspiRepository;
    private final IncidentMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public IncidentResponseDTO create(UUID sspiId, IncidentCreateRequestDTO dto) {
        SSPI sspi = sspiRepository.findById(sspiId)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        IncidentSSPI entity = IncidentSSPI.builder()
                .sspi(sspi)
                .type(dto.getType().trim())
                .gravite(dto.getGravite().trim())
                .description(normalize(dto.getDescription()))
                .action(normalize(dto.getAction()))
                .declaredAt(LocalDateTime.now())
                .declaredBy(getCurrentUserStrict())
                .resolu(false)
                .build();

        IncidentSSPI saved = repository.save(entity);

        audit(
                "SSPI_INCIDENT_CREATE",
                saved.getIncidentId(),
                "Creation incident sspi=" + sspiId
                        + " type=" + saved.getType()
                        + " gravite=" + saved.getGravite()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<IncidentResponseDTO> findBySspi(UUID sspiId, Pageable pageable) {
        return repository.findBySspiSspiIdOrderByDeclaredAtDesc(sspiId, pageable)
                .map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public IncidentResponseDTO getById(UUID id) {
        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));
        return mapper.toDTO(entity);
    }

    public IncidentResponseDTO update(UUID id, IncidentUpdateRequestDTO dto) {
        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        entity.setType(dto.getType().trim());
        entity.setGravite(dto.getGravite().trim());
        entity.setDescription(normalize(dto.getDescription()));
        entity.setAction(normalize(dto.getAction()));

        IncidentSSPI saved = repository.save(entity);

        audit(
                "SSPI_INCIDENT_UPDATE",
                saved.getIncidentId(),
                "Mise a jour incident sspi=" + saved.getSspi().getSspiId()
                        + " type=" + saved.getType()
                        + " gravite=" + saved.getGravite()
        );

        return mapper.toDTO(saved);
    }

    public IncidentResponseDTO resolve(UUID id, IncidentResolveRequestDTO dto) {
        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        entity.setResolu(true);
        entity.setDateResolution(LocalDateTime.now());
        entity.setResolvedBy(getCurrentUserStrict());

        if (StringUtils.hasText(dto.getAction())) {
            entity.setAction(dto.getAction().trim());
        }

        IncidentSSPI saved = repository.save(entity);

        audit(
                "SSPI_INCIDENT_RESOLVE",
                saved.getIncidentId(),
                "Resolution incident sspi=" + saved.getSspi().getSspiId()
                        + " type=" + saved.getType()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        IncidentSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Incident not found"));

        audit(
                "SSPI_INCIDENT_DELETE",
                entity.getIncidentId(),
                "Suppression incident sspi=" + entity.getSspi().getSspiId()
                        + " type=" + entity.getType()
        );

        repository.delete(entity);
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
