package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.AlertSeverity;
import com.tn.softsys.blocoperatoire.domain.AlertType;
import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.SurveillanceSSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.SurveillanceSSPIRequestDTO;
import com.tn.softsys.blocoperatoire.dto.sspi.SurveillanceSSPIResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.SurveillanceSSPIMapper;
import com.tn.softsys.blocoperatoire.repository.IncidentSSPIRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import com.tn.softsys.blocoperatoire.repository.SurveillanceSSPIRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SurveillanceSSPIService {

    private static final String MODULE = "SSPI_SURVEILLANCE";

    private final SurveillanceSSPIRepository repository;
    private final SSPIRepository sspiRepository;
    private final IncidentSSPIRepository incidentRepository;
    private final SurveillanceSSPIMapper mapper;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;
    private final AlertService alertService;

    public SurveillanceSSPIResponseDTO create(UUID sspiId, SurveillanceSSPIRequestDTO dto) {
        SSPI sspi = sspiRepository.findById(sspiId)
                .orElseThrow(() -> new ResourceNotFoundException("SSPI not found"));

        SurveillanceSSPI entity = SurveillanceSSPI.builder()
                .sspi(sspi)
                .dateMesure(dto.getDateMesure() != null ? dto.getDateMesure() : LocalDateTime.now())
                .frequenceCardiaque(dto.getFrequenceCardiaque())
                .paSystolique(dto.getPaSystolique())
                .paDiastolique(dto.getPaDiastolique())
                .frequenceRespiratoire(dto.getFrequenceRespiratoire())
                .spo2(dto.getSpo2())
                .temperatureDixieme(dto.getTemperatureDixieme())
                .douleurEva(dto.getDouleurEva())
                .scoreConscience(dto.getScoreConscience())
                .observations(normalize(dto.getObservations()))
                .mesureePar(getCurrentUserStrict())
                .build();

        SurveillanceSSPI saved = repository.save(entity);
        syncAutomaticIncidents(saved);

        audit("SSPI_SURVEILLANCE_CREATE", saved.getSurveillanceId(),
                "Creation surveillance sspi=" + sspiId
                        + " dateMesure=" + saved.getDateMesure()
                        + " fc=" + saved.getFrequenceCardiaque()
                        + " spo2=" + saved.getSpo2()
                        + " eva=" + saved.getDouleurEva());

        return mapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<SurveillanceSSPIResponseDTO> findBySspi(UUID sspiId, Pageable pageable) {
        return repository.findBySspiSspiIdOrderByDateMesureDesc(sspiId, pageable).map(mapper::toDTO);
    }

    @Transactional(readOnly = true)
    public SurveillanceSSPIResponseDTO getById(UUID id) {
        SurveillanceSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surveillance SSPI not found"));
        return mapper.toDTO(entity);
    }

    public SurveillanceSSPIResponseDTO update(UUID id, SurveillanceSSPIRequestDTO dto) {
        SurveillanceSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surveillance SSPI not found"));

        entity.setDateMesure(dto.getDateMesure() != null ? dto.getDateMesure() : entity.getDateMesure());
        entity.setFrequenceCardiaque(dto.getFrequenceCardiaque());
        entity.setPaSystolique(dto.getPaSystolique());
        entity.setPaDiastolique(dto.getPaDiastolique());
        entity.setFrequenceRespiratoire(dto.getFrequenceRespiratoire());
        entity.setSpo2(dto.getSpo2());
        entity.setTemperatureDixieme(dto.getTemperatureDixieme());
        entity.setDouleurEva(dto.getDouleurEva());
        entity.setScoreConscience(dto.getScoreConscience());
        entity.setObservations(normalize(dto.getObservations()));

        SurveillanceSSPI saved = repository.save(entity);
        syncAutomaticIncidents(saved);

        audit("SSPI_SURVEILLANCE_UPDATE", saved.getSurveillanceId(),
                "Mise a jour surveillance sspi=" + saved.getSspi().getSspiId()
                        + " dateMesure=" + saved.getDateMesure());

        return mapper.toDTO(saved);
    }

    public void delete(UUID id) {
        SurveillanceSSPI entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Surveillance SSPI not found"));

        audit("SSPI_SURVEILLANCE_DELETE", entity.getSurveillanceId(),
                "Suppression surveillance sspi=" + entity.getSspi().getSspiId()
                        + " dateMesure=" + entity.getDateMesure());

        repository.delete(entity);
    }

    private void syncAutomaticIncidents(SurveillanceSSPI surveillance) {
        syncThresholdIncident(surveillance, "DESATURATION",
                surveillance.getSpo2() != null && surveillance.getSpo2() < 92,
                surveillance.getSpo2() != null && surveillance.getSpo2() < 90 ? "CRITICAL" : "MODERATE",
                surveillance.getSpo2() != null ? "Alerte automatique SSPI: SpO2 " + surveillance.getSpo2() + "%" : null);
        syncThresholdIncident(surveillance, "HYPOTENSION",
                surveillance.getPaSystolique() != null && surveillance.getPaSystolique() < 90,
                surveillance.getPaSystolique() != null && surveillance.getPaSystolique() < 80 ? "CRITICAL" : "MODERATE",
                surveillance.getPaSystolique() != null ? "Alerte automatique SSPI: TA systolique " + surveillance.getPaSystolique() + " mmHg" : null);
        syncThresholdIncident(surveillance, "HYPERTENSION",
                surveillance.getPaSystolique() != null && surveillance.getPaSystolique() > 160,
                surveillance.getPaSystolique() != null && surveillance.getPaSystolique() > 180 ? "CRITICAL" : "MODERATE",
                surveillance.getPaSystolique() != null ? "Alerte automatique SSPI: TA systolique " + surveillance.getPaSystolique() + " mmHg" : null);
        syncThresholdIncident(surveillance, "TACHYCARDIA",
                surveillance.getFrequenceCardiaque() != null && surveillance.getFrequenceCardiaque() > 120,
                surveillance.getFrequenceCardiaque() != null && surveillance.getFrequenceCardiaque() > 140 ? "CRITICAL" : "MODERATE",
                surveillance.getFrequenceCardiaque() != null ? "Alerte automatique SSPI: FC " + surveillance.getFrequenceCardiaque() + " bpm" : null);
    }

    private void syncThresholdIncident(SurveillanceSSPI surveillance, String type, boolean triggered, String severity, String description) {
        UUID sspiId = surveillance.getSspi().getSspiId();
        Optional<IncidentSSPI> existing = incidentRepository.findFirstBySspiSspiIdAndTypeAndResoluFalse(sspiId, type);
        User currentUser = getCurrentUserStrict();

        if (triggered) {
            if (existing.isPresent()) {
                return;
            }

            IncidentSSPI created = incidentRepository.save(
                    IncidentSSPI.builder()
                            .sspi(surveillance.getSspi())
                            .type(type)
                            .gravite(severity)
                            .description(description)
                            .action("Incident genere automatiquement depuis une surveillance SSPI.")
                            .declaredAt(LocalDateTime.now())
                            .declaredBy(currentUser)
                            .resolu(false)
                            .build()
            );

            audit("SSPI_INCIDENT_AUTO_CREATE", created.getIncidentId(),
                    "Incident automatique sspi=" + sspiId + " type=" + created.getType() + " gravite=" + created.getGravite());
            syncAutomaticAlert(surveillance, created, false);
            return;
        }

        if (existing.isPresent()) {
            IncidentSSPI resolved = existing.get();
            resolved.setResolu(true);
            resolved.setDateResolution(LocalDateTime.now());
            resolved.setResolvedBy(currentUser);
            if (!StringUtils.hasText(resolved.getAction())) {
                resolved.setAction("Resolution automatique apres normalisation des constantes.");
            }
            incidentRepository.save(resolved);
            audit("SSPI_INCIDENT_AUTO_RESOLVE", resolved.getIncidentId(),
                    "Resolution automatique incident sspi=" + sspiId + " type=" + resolved.getType());
            syncAutomaticAlert(surveillance, resolved, true);
        }
    }

    private void syncAutomaticAlert(SurveillanceSSPI surveillance, IncidentSSPI incident, boolean resolved) {
        if (surveillance.getSspi() == null || surveillance.getSspi().getIntervention() == null) {
            return;
        }

        AlertType alertType = mapIncidentTypeToAlertType(incident.getType());

        if (alertType == null) {
            return;
        }

        if (resolved) {
            alertService.resolveSspiAutomaticVitalAlerts(
                    surveillance.getSspi().getIntervention().getInterventionId(),
                    alertType
            );
            return;
        }

        alertService.createSspiAutomaticVitalAlert(
                surveillance.getSspi(),
                alertType,
                mapIncidentSeverityToAlertSeverity(incident.getGravite()),
                buildAutomaticAlertTitle(incident.getType()),
                incident.getDescription()
        );
    }

    private AlertType mapIncidentTypeToAlertType(String type) {
        if ("DESATURATION".equalsIgnoreCase(type)) {
            return AlertType.SSPI_DESATURATION;
        }

        if ("HYPOTENSION".equalsIgnoreCase(type)) {
            return AlertType.SSPI_HYPOTENSION;
        }

        if ("HYPERTENSION".equalsIgnoreCase(type)) {
            return AlertType.SSPI_HYPERTENSION;
        }

        if ("TACHYCARDIA".equalsIgnoreCase(type)) {
            return AlertType.SSPI_TACHYCARDIA;
        }

        return null;
    }

    private AlertSeverity mapIncidentSeverityToAlertSeverity(String severity) {
        if ("CRITICAL".equalsIgnoreCase(severity) || "SEVERE".equalsIgnoreCase(severity)) {
            return AlertSeverity.CRITICAL;
        }

        if ("MODERATE".equalsIgnoreCase(severity) || "WARNING".equalsIgnoreCase(severity)) {
            return AlertSeverity.WARNING;
        }

        return AlertSeverity.INFO;
    }

    private String buildAutomaticAlertTitle(String type) {
        if ("DESATURATION".equalsIgnoreCase(type)) {
            return "Desaturation SSPI";
        }

        if ("HYPOTENSION".equalsIgnoreCase(type)) {
            return "Hypotension SSPI";
        }

        if ("HYPERTENSION".equalsIgnoreCase(type)) {
            return "Hypertension SSPI";
        }

        if ("TACHYCARDIA".equalsIgnoreCase(type)) {
            return "Tachycardie SSPI";
        }

        return "Alerte SSPI";
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
        auditLogService.log(getCurrentUserStrict(), action, MODULE, referenceId, details, auditContextService.getClientIp());
    }
}
