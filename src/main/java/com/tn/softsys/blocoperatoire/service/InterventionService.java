package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.intervention.InterventionRequestDTO;
import com.tn.softsys.blocoperatoire.dto.intervention.InterventionResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.InterventionMapper;
import com.tn.softsys.blocoperatoire.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InterventionService {

    private static final String MODULE = "PLANIFICATION";

    private final AlertService alertService;
    private final InterventionRepository repository;
    private final PatientRepository patientRepository;
    private final SalleRepository salleRepository;
    private final PlanningBlocRepository planningRepository;
    private final UserRepository userRepository;
    private final SSPIRepository sspiRepository;
    private final InterventionCatalogRepository interventionCatalogRepository;
    private final InterventionMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public InterventionResponseDTO create(InterventionRequestDTO dto) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Salle salle = dto.getSalleId() != null
                ? salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"))
                : null;

        PlanningBloc planning = dto.getPlanningId() != null
                ? planningRepository.findById(dto.getPlanningId())
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found"))
                : null;

        User chirurgien = dto.getChirurgienId() != null
                ? userRepository.findById(dto.getChirurgienId())
                .orElseThrow(() -> new ResourceNotFoundException("Chirurgien not found"))
                : null;

        User anesthesiste = dto.getAnesthesisteId() != null
                ? userRepository.findById(dto.getAnesthesisteId())
                .orElseThrow(() -> new ResourceNotFoundException("Anesthesiste not found"))
                : null;

        InterventionCatalog catalog = resolveCatalog(dto.getCatalogId());

        validateSalleAvailability(salle, dto.getStatut());

        Intervention intervention = mapper.toEntity(
                dto, patient, salle, planning, chirurgien, anesthesiste
        );

        applyCatalog(intervention, catalog);

        Intervention saved = repository.save(intervention);

        if (saved.getStatut() == StatutIntervention.PLANIFIEE) {
            alertService.createInterventionPlannedAlert(saved);
        }

        if (saved.getStatut() == StatutIntervention.EN_SSPI) {
            createSspiIfMissing(saved);
        }

        audit(
                "INTERVENTION_CREATE",
                saved.getInterventionId(),
                "Creation intervention patient=" + saved.getPatient().getPatientId()
                        + " nom=" + saved.getNomIntervention()
                        + " statut=" + saved.getStatut()
                        + " salle=" + (saved.getSalle() != null ? saved.getSalle().getSalleId() : null)
        );

        return mapper.toResponse(saved);
    }

    public InterventionResponseDTO update(UUID id, InterventionRequestDTO dto) {
        Intervention existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        validateEditablePlanning(existing);

        StatutIntervention previousStatut = existing.getStatut();
        String previousSlot = formatPlanningSlot(existing);
        UUID previousSalleId = existing.getSalle() != null ? existing.getSalle().getSalleId() : null;
        var previousDate = existing.getDateIntervention();
        var previousStartTime = existing.getHeureDebut();

        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Salle salle = dto.getSalleId() != null
                ? salleRepository.findById(dto.getSalleId())
                .orElseThrow(() -> new ResourceNotFoundException("Salle not found"))
                : null;

        PlanningBloc planning = dto.getPlanningId() != null
                ? planningRepository.findById(dto.getPlanningId())
                .orElseThrow(() -> new ResourceNotFoundException("Planning not found"))
                : null;

        User chirurgien = dto.getChirurgienId() != null
                ? userRepository.findById(dto.getChirurgienId())
                .orElseThrow(() -> new ResourceNotFoundException("Chirurgien not found"))
                : null;

        User anesthesiste = dto.getAnesthesisteId() != null
                ? userRepository.findById(dto.getAnesthesisteId())
                .orElseThrow(() -> new ResourceNotFoundException("Anesthesiste not found"))
                : null;

        InterventionCatalog catalog = resolveCatalog(dto.getCatalogId());

        validateSalleAvailability(salle, dto.getStatut());

        mapper.updateEntity(existing, dto, patient, salle, planning, chirurgien, anesthesiste);
        applyCatalog(existing, catalog);

        Intervention saved = repository.save(existing);
        UUID nextSalleId = saved.getSalle() != null ? saved.getSalle().getSalleId() : null;
        boolean scheduleChanged =
                !Objects.equals(previousDate, saved.getDateIntervention())
                        || !Objects.equals(previousStartTime, saved.getHeureDebut())
                        || !Objects.equals(previousSalleId, nextSalleId);

        if (saved.getStatut() == StatutIntervention.PLANIFIEE) {
            if (previousStatut == StatutIntervention.PLANIFIEE && scheduleChanged) {
                alertService.createInterventionRescheduledAlert(
                        saved,
                        previousSlot,
                        formatPlanningSlot(saved)
                );
            } else if (previousStatut != StatutIntervention.PLANIFIEE) {
                alertService.createInterventionPlannedAlert(saved);
            }
        }

        if (saved.getStatut() == StatutIntervention.EN_SSPI) {
            createSspiIfMissing(saved);
        }

        audit(
                "INTERVENTION_UPDATE",
                saved.getInterventionId(),
                "Mise a jour intervention patient=" + saved.getPatient().getPatientId()
                        + " nom=" + saved.getNomIntervention()
                        + " statut=" + saved.getStatut()
                        + " salle=" + (saved.getSalle() != null ? saved.getSalle().getSalleId() : null)
        );

        return mapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public InterventionResponseDTO getById(UUID id) {
        Intervention intervention = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        return mapper.toResponse(intervention);
    }

    public Page<InterventionResponseDTO> search(
            UUID patientId,
            StatutIntervention statut,
            Boolean urgenceOMS,
            String codeActe,
            Pageable pageable
    ) {
        return repository.findAll((root, query, cb) -> {
            var predicate = cb.conjunction();

            if (patientId != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("patient").get("patientId"), patientId)
                );
            }

            if (statut != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("statut"), statut)
                );
            }

            if (urgenceOMS != null) {
                predicate = cb.and(
                        predicate,
                        cb.equal(root.get("urgenceOMS"), urgenceOMS)
                );
            }

            if (codeActe != null) {
                predicate = cb.and(
                        predicate,
                        cb.like(cb.lower(root.get("codeActe")), "%" + codeActe.toLowerCase() + "%")
                );
            }

            return predicate;
        }, pageable).map(mapper::toResponse);
    }

    public void delete(UUID id) {
        Intervention intervention = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        audit(
                "INTERVENTION_DELETE",
                intervention.getInterventionId(),
                "Suppression intervention patient=" + intervention.getPatient().getPatientId()
                        + " nom=" + intervention.getNomIntervention()
                        + " statut=" + intervention.getStatut()
        );

        repository.deleteById(id);
    }

    @Transactional
    public InterventionResponseDTO updateStatut(UUID interventionId, StatutIntervention statut) {
        Intervention intervention = repository.findById(interventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        StatutIntervention oldStatut = intervention.getStatut();
        validateSalleAvailability(intervention.getSalle(), statut);
        intervention.setStatut(statut);

        Intervention saved = repository.save(intervention);

        if (statut == StatutIntervention.EN_COURS) {
            alertService.createInterventionStartedAlert(saved);
        }

        if (statut == StatutIntervention.PLANIFIEE) {
            alertService.createInterventionPlannedAlert(saved);
        }

        if (statut == StatutIntervention.EN_SSPI) {
            createSspiIfMissing(saved);
        }

        audit(
                "INTERVENTION_STATUS_UPDATE",
                saved.getInterventionId(),
                "Changement statut intervention ancien=" + oldStatut
                        + " nouveau=" + saved.getStatut()
                        + " patient=" + saved.getPatient().getPatientId()
                        + " nom=" + saved.getNomIntervention()
        );

        return mapper.toResponse(saved);
    }

    private void validateSalleAvailability(Salle salle, StatutIntervention statut) {
        if (salle == null || statut == null) {
            return;
        }

        boolean roomMustBeOperational = statut == StatutIntervention.PLANIFIEE
                || statut == StatutIntervention.EN_COURS;

        if (!roomMustBeOperational) {
            return;
        }

        StatutSalle managedStatus = salle.getStatut();
        boolean operational = managedStatus != null
                ? managedStatus.isOperational()
                : Boolean.TRUE.equals(salle.getActive());

        if (!operational) {
            String roomLabel = salle.getNom() != null && !salle.getNom().isBlank()
                    ? salle.getNom().trim()
                    : "Salle inconnue";
            throw new IllegalStateException(
                    "Impossible de planifier ou demarrer une intervention sur une salle indisponible: "
                            + roomLabel
                            + " (statut "
                            + formatSalleStatus(managedStatus, salle.getActive())
                            + ")"
            );
        }
    }

    private String formatSalleStatus(StatutSalle statutSalle, Boolean active) {
        if (statutSalle == null) {
            return Boolean.TRUE.equals(active) ? "DISPONIBLE" : "FERMEE";
        }
        return statutSalle.name();
    }

    private InterventionCatalog resolveCatalog(UUID catalogId) {
        if (catalogId == null) {
            return null;
        }

        return interventionCatalogRepository.findById(catalogId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention catalog not found"));
    }

    private void applyCatalog(Intervention intervention, InterventionCatalog catalog) {
        intervention.setCatalog(catalog);

        if (catalog == null) {
            return;
        }

        if (catalog.getDesignation() != null && !catalog.getDesignation().isBlank()) {
            intervention.setNomIntervention(catalog.getDesignation().trim());
        }

        if (catalog.getDesignationEn() != null && !catalog.getDesignationEn().isBlank()) {
            intervention.setNomInterventionEn(catalog.getDesignationEn().trim());
        }

        if (catalog.getDesignationAr() != null && !catalog.getDesignationAr().isBlank()) {
            intervention.setNomInterventionAr(catalog.getDesignationAr().trim());
        }

        if (catalog.getDureeMinutes() != null && catalog.getDureeMinutes() > 0) {
            intervention.setDureePrevue(catalog.getDureeMinutes());
        }
    }

    private void createSspiIfMissing(Intervention intervention) {
        if (intervention == null || intervention.getInterventionId() == null) {
            return;
        }

        sspiRepository.findByInterventionInterventionId(intervention.getInterventionId())
                .orElseGet(() -> {
                    SSPI sspi = SSPI.builder()
                            .intervention(intervention)
                            .heureEntree(LocalDateTime.now())
                            .build();

                    return sspiRepository.save(sspi);
                });
    }

    private String formatPlanningSlot(Intervention intervention) {
        if (intervention == null) {
            return "creneau inconnu";
        }

        String dateLabel = intervention.getDateIntervention() != null
                ? intervention.getDateIntervention().toString()
                : "date inconnue";
        String timeLabel = intervention.getHeureDebut() != null
                ? intervention.getHeureDebut().toString()
                : "heure non precisee";
        String roomLabel =
                intervention.getSalle() != null && intervention.getSalle().getNom() != null && !intervention.getSalle().getNom().isBlank()
                        ? intervention.getSalle().getNom().trim()
                        : "sans salle";

        return dateLabel + " " + timeLabel + " · " + roomLabel;
    }

    private void validateEditablePlanning(Intervention intervention) {
        if (intervention == null) {
            return;
        }

        if (intervention.getStatut() != StatutIntervention.PLANIFIEE) {
            throw new IllegalStateException("Only scheduled interventions can be edited");
        }

        if (isPlanningSlotPassed(intervention.getDateIntervention(), intervention.getHeureDebut())) {
            throw new IllegalStateException("Cannot edit an intervention whose scheduled date has passed");
        }
    }

    private boolean isPlanningSlotPassed(LocalDate dateIntervention, LocalTime heureDebut) {
        if (dateIntervention == null) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();

        if (heureDebut == null) {
            return dateIntervention.isBefore(now.toLocalDate());
        }

        return LocalDateTime.of(dateIntervention, heureDebut).isBefore(now);
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
