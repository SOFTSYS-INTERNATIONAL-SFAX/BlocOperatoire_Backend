package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.TraitementSSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.TraitementSSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.TraitementSSPIResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.TraitementSSPIMapper;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import com.tn.softsys.blocoperatoire.repository.TraitementSSPIRepository;
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
public class TraitementSSPIService {

    private static final String MODULE = "SSPI_TREATMENT";

    private final TraitementSSPIRepository repository;
    private final SSPIRepository sspiRepository;
    private final TraitementSSPIMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public TraitementSSPIResponseDTO create(UUID sspiId, TraitementSSPIRequestDTO dto) {
        SSPI sspi = sspiRepository.findById(sspiId)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        User currentUser = getCurrentUserStrict();

        TraitementSSPI entity = TraitementSSPI.builder()
                .sspi(sspi)
                .nom(dto.getNom().trim())
                .dose(normalize(dto.getDose()))
                .voieAdministration(normalize(dto.getVoieAdministration()))
                .heureAdministration(dto.getHeureAdministration() != null ? dto.getHeureAdministration() : LocalDateTime.now())
                .observations(normalize(dto.getObservations()))
                .administrePar(currentUser)
                .build();

        TraitementSSPI saved = repository.save(entity);

        audit(
                "SSPI_TREATMENT_CREATE",
                saved.getTraitementId(),
                "Creation traitement sspi=" + sspiId
                        + " nom=" + saved.getNom()
                        + " dose=" + saved.getDose()
                        + " voie=" + saved.getVoieAdministration()
                        + " heure=" + saved.getHeureAdministration()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<TraitementSSPIResponseDTO> findBySspi(UUID sspiId, Pageable pageable) {
        return repository.findBySspiSspiIdOrderByHeureAdministrationDesc(sspiId, pageable)
                .map(mapper::toDTO);
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
