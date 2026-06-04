package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Autopsie;
import com.tn.softsys.blocoperatoire.domain.AutopsieStatut;
import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.dto.autopsie.AutopsieRequestDTO;
import com.tn.softsys.blocoperatoire.dto.autopsie.AutopsieResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.AutopsieMapper;
import com.tn.softsys.blocoperatoire.repository.AutopsieRepository;
import com.tn.softsys.blocoperatoire.repository.DecesRepository;
import jakarta.persistence.criteria.JoinType;
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
public class AutopsieService {

    private static final String MODULE = "AUTOPSIE";

    private final AutopsieRepository repository;
    private final DecesRepository decesRepository;
    private final AutopsieMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public AutopsieResponseDTO create(AutopsieRequestDTO dto) {
        if (repository.existsByDecesDecesId(dto.getDecesId())) {
            throw new IllegalStateException("Autopsie already exists for this deces");
        }

        Deces deces = decesRepository.findById(dto.getDecesId())
                .orElseThrow(() -> new ResourceNotFoundException("Deces not found"));

        Autopsie entity = Autopsie.builder()
                .deces(deces)
                .datePrevue(dto.getDatePrevue())
                .dateRealisee(dto.getDateRealisee())
                .medecinLegiste(dto.getMedecinLegiste())
                .statut(dto.getStatut() != null ? dto.getStatut() : AutopsieStatut.PLANIFIEE)
                .rapport(dto.getRapport())
                .observations(dto.getObservations())
                .build();

        Autopsie saved = repository.save(entity);

        audit(
                "AUTOPSIE_CREATE",
                saved.getAutopsieId(),
                "Creation autopsie deces=" + saved.getDeces().getDecesId()
                        + " statut=" + saved.getStatut()
                        + " medecin=" + saved.getMedecinLegiste()
        );

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public AutopsieResponseDTO getById(UUID id) {
        Autopsie entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autopsie not found"));

        return mapper.toDTO(entity);
    }

    @Transactional(readOnly = true)
    public Page<AutopsieResponseDTO> search(
            UUID decesId,
            UUID morgueId,
            AutopsieStatut statut,
            LocalDateTime from,
            LocalDateTime to,
            String q,
            Pageable pageable) {

        return repository.findAll((root, query, cb) -> {
            var predicate = cb.conjunction();

            var decesJoin = root.join("deces", JoinType.LEFT);
            var interventionJoin = decesJoin.join("intervention", JoinType.LEFT);
            var patientJoin = interventionJoin.join("patient", JoinType.LEFT);
            var caseJoin = decesJoin.join("caseMortuaire", JoinType.LEFT);
            var morgueJoin = caseJoin.join("morgue", JoinType.LEFT);

            if (decesId != null) {
                predicate = cb.and(predicate, cb.equal(decesJoin.get("decesId"), decesId));
            }

            if (morgueId != null) {
                predicate = cb.and(predicate, cb.equal(morgueJoin.get("morgueId"), morgueId));
            }

            if (statut != null) {
                predicate = cb.and(predicate, cb.equal(root.get("statut"), statut));
            }

            if (from != null) {
                predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("datePrevue"), from));
            }

            if (to != null) {
                predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("datePrevue"), to));
            }

            if (q != null && !q.isBlank()) {
                String like = "%" + q.trim().toLowerCase() + "%";

                predicate = cb.and(predicate, cb.or(
                        cb.like(cb.lower(root.get("medecinLegiste")), like),
                        cb.like(cb.lower(root.get("rapport")), like),
                        cb.like(cb.lower(root.get("observations")), like),
                        cb.like(cb.lower(patientJoin.get("nom")), like),
                        cb.like(cb.lower(patientJoin.get("prenom")), like),
                        cb.like(cb.lower(patientJoin.get("mrn")), like),
                        cb.like(cb.lower(morgueJoin.get("nom")), like),
                        cb.like(cb.lower(caseJoin.get("numeroCase")), like)
                ));
            }

            query.distinct(true);
            return predicate;
        }, pageable).map(mapper::toDTO);
    }

    public AutopsieResponseDTO update(UUID id, AutopsieRequestDTO dto) {
        Autopsie entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autopsie not found"));

        entity.setDatePrevue(dto.getDatePrevue());
        entity.setDateRealisee(dto.getDateRealisee());
        entity.setMedecinLegiste(dto.getMedecinLegiste());
        entity.setStatut(dto.getStatut() != null ? dto.getStatut() : entity.getStatut());
        entity.setRapport(dto.getRapport());
        entity.setObservations(dto.getObservations());

        Autopsie saved = repository.save(entity);

        audit(
                "AUTOPSIE_UPDATE",
                saved.getAutopsieId(),
                "Mise a jour autopsie statut=" + saved.getStatut()
                        + " datePrevue=" + saved.getDatePrevue()
                        + " dateRealisee=" + saved.getDateRealisee()
        );

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        Autopsie entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Autopsie not found"));

        audit(
                "AUTOPSIE_DELETE",
                entity.getAutopsieId(),
                "Suppression autopsie deces=" + entity.getDeces().getDecesId()
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
