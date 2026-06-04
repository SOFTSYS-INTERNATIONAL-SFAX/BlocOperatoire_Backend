package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.dto.deces.DecesRequestDTO;
import com.tn.softsys.blocoperatoire.dto.deces.DecesResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.DecesMapper;
import com.tn.softsys.blocoperatoire.repository.DecesRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
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
public class DecesService {

    private static final String MODULE = "DECES";

    private final DecesRepository repository;
    private final InterventionRepository interventionRepository;
    private final DecesMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public DecesResponseDTO create(DecesRequestDTO dto) {
        if (repository.existsByInterventionInterventionId(dto.getInterventionId())) {
            throw new IllegalStateException("Deces already declared for this intervention");
        }

        Intervention intervention = interventionRepository.findById(dto.getInterventionId())
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        Deces entity = Deces.builder()
                .intervention(intervention)
                .dateDeces(dto.getDateDeces())
                .cause(dto.getCause())
                .constatPar(dto.getConstatPar())
                .build();

        Deces saved = repository.save(entity);

        audit(
                "DECES_CREATE",
                saved.getDecesId(),
                "Creation deces intervention=" + saved.getIntervention().getInterventionId()
                        + " cause=" + saved.getCause()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public DecesResponseDTO getById(UUID id) {
        Deces entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<DecesResponseDTO> search(
            LocalDateTime from,
            LocalDateTime to,
            UUID interventionId,
            Boolean nonAffecte,
            String q,
            Pageable pageable) {

        return repository.findAll((root, query, cb) -> {
            var predicate = cb.conjunction();

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateDeces"), from));
            }

            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateDeces"), to));
            }

            if (interventionId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("intervention").get("interventionId"), interventionId)
                );
            }

            if (Boolean.TRUE.equals(nonAffecte)) {
                predicate = cb.and(predicate, cb.isNull(root.get("caseMortuaire")));
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";
                var interventionJoin = root.join("intervention", jakarta.persistence.criteria.JoinType.LEFT);
                var patientJoin = interventionJoin.join("patient", jakarta.persistence.criteria.JoinType.LEFT);

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("cause")), like),
                        cb.like(cb.lower(root.get("constatPar")), like),
                        cb.like(cb.lower(patientJoin.get("nom")), like),
                        cb.like(cb.lower(patientJoin.get("prenom")), like),
                        cb.like(cb.lower(patientJoin.get("mrn")), like)
                ));
            }

            query.distinct(true);
            return predicate;
        }, pageable).map(mapper::toDTO);
    }

    public DecesResponseDTO update(UUID id, DecesRequestDTO dto) {
        Deces entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        entity.setDateDeces(dto.getDateDeces());
        entity.setCause(dto.getCause());
        entity.setConstatPar(dto.getConstatPar());

        Deces saved = repository.save(entity);

        audit(
                "DECES_UPDATE",
                saved.getDecesId(),
                "Mise a jour deces cause=" + saved.getCause()
                        + " constatPar=" + saved.getConstatPar()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        Deces entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        audit(
                "DECES_DELETE",
                entity.getDecesId(),
                "Suppression deces intervention=" + entity.getIntervention().getInterventionId()
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
