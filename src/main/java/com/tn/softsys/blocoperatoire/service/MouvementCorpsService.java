package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsRequestDTO;
import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.MouvementCorpsMapper;
import com.tn.softsys.blocoperatoire.repository.CaseMortuaireRepository;
import com.tn.softsys.blocoperatoire.repository.MouvementCorpsRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class MouvementCorpsService {

    private static final String MODULE = "MOUVEMENT_CORPS";
    private static final Set<String> ALLOWED_TYPES = Set.of("TRANSFERT", "AUTOPSIE", "SORTIE", "DEPOT");

    private final MouvementCorpsRepository repository;
    private final CaseMortuaireRepository caseRepository;
    private final MouvementCorpsMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public MouvementCorpsResponseDTO create(MouvementCorpsRequestDTO dto) {
        CaseMortuaire caseMortuaire = caseRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        String typeMouvement = normalizeType(dto.getTypeMouvement());

        MouvementCorps entity = MouvementCorps.builder()
                .caseMortuaire(caseMortuaire)
                .dateMouvement(dto.getDateMouvement())
                .typeMouvement(typeMouvement)
                .build();

        MouvementCorps saved = repository.save(entity);

        audit(
                "MOUVEMENT_CREATE",
                saved.getMouvementId(),
                "Creation mouvement type=" + saved.getTypeMouvement()
                        + " case=" + saved.getCaseMortuaire().getCaseId()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public MouvementCorpsResponseDTO getById(UUID id) {
        MouvementCorps entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mouvement not found"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<MouvementCorpsResponseDTO> search(
            UUID caseId,
            UUID morgueId,
            UUID decesId,
            LocalDateTime from,
            LocalDateTime to,
            String type,
            String q,
            Pageable pageable) {

        return repository.findAll((root, query, cb) -> {
            var predicate = cb.conjunction();

            var caseJoin = root.join("caseMortuaire", JoinType.LEFT);
            var morgueJoin = caseJoin.join("morgue", JoinType.LEFT);
            var decesJoin = caseJoin.join("deces", JoinType.LEFT);
            var interventionJoin = decesJoin.join("intervention", JoinType.LEFT);
            var patientJoin = interventionJoin.join("patient", JoinType.LEFT);

            if (caseId != null) {
                predicate = cb.and(predicate, cb.equal(caseJoin.get("caseId"), caseId));
            }

            if (morgueId != null) {
                predicate = cb.and(predicate, cb.equal(morgueJoin.get("morgueId"), morgueId));
            }

            if (decesId != null) {
                predicate = cb.and(predicate, cb.equal(decesJoin.get("decesId"), decesId));
            }

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("dateMouvement"), from));
            }

            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("dateMouvement"), to));
            }

            if (type != null && !type.isBlank()) {
                predicate = cb.and(
                        predicate,
                        cb.equal(cb.upper(root.get("typeMouvement")), type.trim().toUpperCase())
                );
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("typeMouvement")), like),
                        cb.like(cb.lower(caseJoin.get("numeroCase")), like),
                        cb.like(cb.lower(morgueJoin.get("nom")), like),
                        cb.like(cb.lower(patientJoin.get("nom")), like),
                        cb.like(cb.lower(patientJoin.get("prenom")), like),
                        cb.like(cb.lower(patientJoin.get("mrn")), like)
                ));
            }

            query.distinct(true);
            return predicate;
        }, pageable).map(mapper::toDTO);
    }

    public MouvementCorpsResponseDTO update(UUID id, MouvementCorpsRequestDTO dto) {
        MouvementCorps entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mouvement not found"));

        CaseMortuaire caseMortuaire = caseRepository.findById(dto.getCaseId())
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        entity.setCaseMortuaire(caseMortuaire);
        entity.setDateMouvement(dto.getDateMouvement());
        entity.setTypeMouvement(normalizeType(dto.getTypeMouvement()));

        MouvementCorps saved = repository.save(entity);

        audit(
                "MOUVEMENT_UPDATE",
                saved.getMouvementId(),
                "Mise a jour mouvement type=" + saved.getTypeMouvement()
                        + " case=" + saved.getCaseMortuaire().getCaseId()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        MouvementCorps entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mouvement not found"));

        audit(
                "MOUVEMENT_DELETE",
                entity.getMouvementId(),
                "Suppression mouvement type=" + entity.getTypeMouvement()
        );

        repository.deleteById(id);
    }

    private String normalizeType(String value) {
        String normalized = value == null ? "" : value.trim().toUpperCase();

        if (!ALLOWED_TYPES.contains(normalized)) {
            throw new IllegalArgumentException("Invalid typeMouvement");
        }

        return normalized;
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
