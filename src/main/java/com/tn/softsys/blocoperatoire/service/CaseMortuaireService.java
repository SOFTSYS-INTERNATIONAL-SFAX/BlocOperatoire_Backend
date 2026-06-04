package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.domain.Morgue;
import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireRequestDTO;
import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.CaseMortuaireMapper;
import com.tn.softsys.blocoperatoire.repository.CaseMortuaireRepository;
import com.tn.softsys.blocoperatoire.repository.DecesRepository;
import com.tn.softsys.blocoperatoire.repository.MorgueRepository;
import jakarta.persistence.criteria.JoinType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CaseMortuaireService {

    private static final String MODULE = "CASE_MORTUAIRE";

    private final CaseMortuaireRepository repository;
    private final MorgueRepository morgueRepository;
    private final DecesRepository decesRepository;
    private final CaseMortuaireMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

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

        CaseMortuaire saved = repository.save(entity);

        audit(
                "CASE_CREATE",
                saved.getCaseId(),
                "Creation cellule numero=" + saved.getNumeroCase()
                        + " morgue=" + saved.getMorgue().getNom()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public CaseMortuaireResponseDTO getById(UUID id) {
        CaseMortuaire entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<CaseMortuaireResponseDTO> search(
            UUID morgueId,
            Boolean occupee,
            String q,
            Pageable pageable) {

        return repository.findAll((root, query, cb) -> {
            var predicate = cb.conjunction();

            if (morgueId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("morgue").get("morgueId"), morgueId)
                );
            }

            if (occupee != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("occupee"), occupee)
                );
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";

                var morgueJoin = root.join("morgue", JoinType.LEFT);
                var decesJoin = root.join("deces", JoinType.LEFT);
                var interventionJoin = decesJoin.join("intervention", JoinType.LEFT);
                var patientJoin = interventionJoin.join("patient", JoinType.LEFT);

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("numeroCase")), like),
                        cb.like(cb.lower(morgueJoin.get("nom")), like),
                        cb.like(cb.lower(decesJoin.get("cause")), like),
                        cb.like(cb.lower(patientJoin.get("nom")), like),
                        cb.like(cb.lower(patientJoin.get("prenom")), like),
                        cb.like(cb.lower(patientJoin.get("mrn")), like)
                ));
            }

            query.distinct(true);
            return predicate;
        }, pageable).map(mapper::toDTO);
    }

    public CaseMortuaireResponseDTO update(UUID id, CaseMortuaireRequestDTO dto) {
        CaseMortuaire entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (repository.existsByNumeroCaseAndCaseIdNot(dto.getNumeroCase(), id)) {
            throw new IllegalStateException("Numero case already exists");
        }

        Morgue morgue = morgueRepository.findById(dto.getMorgueId())
                .orElseThrow(() -> new ResourceNotFoundException("Morgue not found"));

        entity.setNumeroCase(dto.getNumeroCase());
        entity.setMorgue(morgue);

        CaseMortuaire saved = repository.save(entity);

        audit(
                "CASE_UPDATE",
                saved.getCaseId(),
                "Mise a jour cellule numero=" + saved.getNumeroCase()
                        + " morgue=" + saved.getMorgue().getNom()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        CaseMortuaire entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        audit(
                "CASE_DELETE",
                entity.getCaseId(),
                "Suppression cellule numero=" + entity.getNumeroCase()
        );

        repository.deleteById(id);
    }

    public CaseMortuaireResponseDTO affecter(UUID caseId, UUID decesId) {
        CaseMortuaire caseMortuaire = repository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        if (Boolean.TRUE.equals(caseMortuaire.getOccupee())) {
            throw new IllegalStateException("Case already occupied");
        }

        if (repository.existsByDecesDecesId(decesId)) {
            throw new IllegalStateException("Deces already assigned to another case");
        }

        Deces deces = decesRepository.findById(decesId)
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        caseMortuaire.setDeces(deces);
        caseMortuaire.setOccupee(true);

        CaseMortuaire saved = repository.save(caseMortuaire);

        audit(
                "CASE_AFFECTER",
                saved.getCaseId(),
                "Affectation cellule numero=" + saved.getNumeroCase()
                        + " deces=" + deces.getDecesId()
        );

        return mapper.toDTO(saved);
    }

    public CaseMortuaireResponseDTO liberer(UUID caseId) {
        CaseMortuaire caseMortuaire = repository.findById(caseId)
                .orElseThrow(() -> new ResourceNotFoundException("Case not found"));

        UUID decesId = caseMortuaire.getDeces() != null ? caseMortuaire.getDeces().getDecesId() : null;

        caseMortuaire.setDeces(null);
        caseMortuaire.setOccupee(false);

        CaseMortuaire saved = repository.save(caseMortuaire);

        audit(
                "CASE_LIBERER",
                saved.getCaseId(),
                "Liberation cellule numero=" + saved.getNumeroCase()
                        + " ancienDeces=" + decesId
        );

        return mapper.toDTO(saved);
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
