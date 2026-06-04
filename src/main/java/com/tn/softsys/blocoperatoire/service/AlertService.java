package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.alert.AlertResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.AlertMapper;
import com.tn.softsys.blocoperatoire.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AlertService {

    private final AlertRepository alertRepository;
    private final AlertMapper alertMapper;
    private final SimpMessagingTemplate messagingTemplate;
    private final AuditLogService auditLogService;

    public Alert create(Alert alert) {
        Alert saved = alertRepository.save(alert);

        auditLogService.log(
                null,
                "ALERT_CREATED",
                "ALERT",
                saved.getAlertId(),
                "type=" + saved.getType() + " | title=" + saved.getTitle(),
                null
        );

        messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(saved));
        return saved;
    }

    public AlertResponseDTO createInterventionPlannedAlert(Intervention intervention) {
        validateIntervention(intervention);

        return alertRepository
                .findByTypeAndIntervention_InterventionIdAndActiveTrue(
                        AlertType.INTERVENTION_PLANIFIEE,
                        intervention.getInterventionId()
                )
                .map(alertMapper::toDTO)
                .orElseGet(() -> {
                    Alert alert = Alert.builder()
                            .type(AlertType.INTERVENTION_PLANIFIEE)
                            .severity(AlertSeverity.INFO)
                            .title("Intervention planifiee")
                            .titleEn("Planned procedure")
                            .titleAr("عملية مجدولة")
                            .message(buildInterventionMessage(intervention, "a ete planifiee"))
                            .messageEn(buildInterventionMessageEn(intervention, "has been scheduled"))
                            .messageAr(buildInterventionMessageAr(intervention, "تمت جدولة العملية"))
                            .intervention(intervention)
                            .active(true)
                            .acknowledged(false)
                            .viewed(false)
                            .build();

                    populateLocalizedAlertFields(alert, intervention, intervention.getSalle());
                    return alertMapper.toDTO(create(alert));
                });
    }

    public AlertResponseDTO createInterventionRescheduledAlert(
            Intervention intervention,
            String previousSlot,
            String nextSlot
    ) {
        validateIntervention(intervention);

        closeActiveInterventionAlerts(
                intervention.getInterventionId(),
                List.of(AlertType.INTERVENTION_PLANIFIEE)
        );

        Alert alert = Alert.builder()
                .type(AlertType.INTERVENTION_PLANIFIEE)
                .severity(AlertSeverity.INFO)
                .title("Intervention reprogrammee")
                .titleEn("Rescheduled procedure")
                .titleAr("عملية أعيدت جدولتها")
                .message(buildInterventionRescheduledMessage(intervention, previousSlot, nextSlot))
                .messageEn(buildInterventionRescheduledMessageEn(intervention, previousSlot, nextSlot))
                .messageAr(buildInterventionRescheduledMessageAr(intervention, previousSlot, nextSlot))
                .intervention(intervention)
                .active(true)
                .acknowledged(false)
                .viewed(false)
                .build();

        populateLocalizedAlertFields(alert, intervention, intervention.getSalle());
        return alertMapper.toDTO(create(alert));
    }

    public AlertResponseDTO createInterventionStartedAlert(Intervention intervention) {
        validateIntervention(intervention);

        return alertRepository
                .findByTypeAndIntervention_InterventionIdAndActiveTrue(
                        AlertType.INTERVENTION_DEMARREE,
                        intervention.getInterventionId()
                )
                .map(alertMapper::toDTO)
                .orElseGet(() -> {
                    closeActiveInterventionAlerts(
                            intervention.getInterventionId(),
                            List.of(AlertType.INTERVENTION_PLANIFIEE)
                    );

                    Alert alert = Alert.builder()
                            .type(AlertType.INTERVENTION_DEMARREE)
                            .severity(AlertSeverity.INFO)
                            .title("Intervention demarree")
                            .titleEn("Procedure started")
                            .titleAr("بدأت العملية")
                            .message(buildInterventionMessage(intervention, "a demarre"))
                            .messageEn(buildInterventionMessageEn(intervention, "has started"))
                            .messageAr(buildInterventionMessageAr(intervention, "بدأت العملية"))
                            .intervention(intervention)
                            .active(true)
                            .acknowledged(false)
                            .viewed(false)
                            .build();

                    populateLocalizedAlertFields(alert, intervention, intervention.getSalle());
                    return alertMapper.toDTO(create(alert));
                });
    }

    public AlertResponseDTO createSspiOverrunAlert(SSPI sspi) {
        if (sspi == null || sspi.getIntervention() == null || sspi.getIntervention().getInterventionId() == null) {
            throw new IllegalArgumentException("SSPI and intervention are required");
        }

        UUID interventionId = sspi.getIntervention().getInterventionId();

        return alertRepository
                .findByTypeAndIntervention_InterventionIdAndActiveTrue(
                        AlertType.SSPI_DEPASSEMENT,
                        interventionId
                )
                .map(alertMapper::toDTO)
                .orElseGet(() -> {
                    Intervention intervention = sspi.getIntervention();

                    Alert alert = Alert.builder()
                            .type(AlertType.SSPI_DEPASSEMENT)
                            .severity(AlertSeverity.CRITICAL)
                            .title("Depassement SSPI")
                            .titleEn("PACU overrun")
                            .titleAr("تجاوز مدة الإفاقة")
                            .message(buildSspiOverrunMessage(sspi, intervention))
                            .messageEn(buildSspiOverrunMessageEn(sspi, intervention))
                            .messageAr(buildSspiOverrunMessageAr(sspi, intervention))
                            .intervention(intervention)
                            .active(true)
                            .acknowledged(false)
                            .viewed(false)
                            .build();

                    populateLocalizedAlertFields(alert, intervention, intervention.getSalle());
                    return alertMapper.toDTO(create(alert));
                });
    }

    public AlertResponseDTO createSspiAutomaticVitalAlert(
            SSPI sspi,
            AlertType type,
            AlertSeverity severity,
            String title,
            String message
    ) {
        if (sspi == null || sspi.getIntervention() == null || sspi.getIntervention().getInterventionId() == null) {
            throw new IllegalArgumentException("SSPI and intervention are required");
        }

        UUID interventionId = sspi.getIntervention().getInterventionId();

        return alertRepository
                .findByTypeAndIntervention_InterventionIdAndActiveTrue(type, interventionId)
                .map(alertMapper::toDTO)
                .orElseGet(() -> {
                    Alert alert = Alert.builder()
                            .type(type)
                            .severity(severity)
                            .title(title)
                            .message(message)
                            .titleEn(title)
                            .titleAr(title)
                            .messageEn(message)
                            .messageAr(message)
                            .intervention(sspi.getIntervention())
                            .active(true)
                            .acknowledged(false)
                            .viewed(false)
                            .build();

                    populateLocalizedAlertFields(alert, sspi.getIntervention(), sspi.getIntervention().getSalle());
                    return alertMapper.toDTO(create(alert));
                });
    }

    public AlertResponseDTO createSalleStatusAlert(Salle salle) {
        if (salle == null || salle.getSalleId() == null) {
            throw new IllegalArgumentException("Salle is required");
        }

        AlertType targetType = Boolean.TRUE.equals(salle.getActive())
                ? AlertType.SALLE_ACTIVEE
                : AlertType.SALLE_DESACTIVEE;

        return alertRepository
                .findByTypeAndSalle_SalleIdAndActiveTrue(targetType, salle.getSalleId())
                .map(alertMapper::toDTO)
                .orElseGet(() -> {
                    closeActiveSalleAlerts(
                            salle.getSalleId(),
                            List.of(AlertType.SALLE_ACTIVEE, AlertType.SALLE_DESACTIVEE)
                    );

                    Alert alert = Alert.builder()
                            .type(targetType)
                            .severity(Boolean.TRUE.equals(salle.getActive()) ? AlertSeverity.INFO : AlertSeverity.WARNING)
                            .title(Boolean.TRUE.equals(salle.getActive()) ? "Salle activee" : "Salle desactivee")
                            .titleEn(Boolean.TRUE.equals(salle.getActive()) ? "Room activated" : "Room deactivated")
                            .titleAr(Boolean.TRUE.equals(salle.getActive()) ? "تم تفعيل غرفة العمليات" : "تم تعطيل غرفة العمليات")
                            .message(buildSalleStatusMessage(salle))
                            .messageEn(buildSalleStatusMessageEn(salle))
                            .messageAr(buildSalleStatusMessageAr(salle))
                            .salle(salle)
                            .active(true)
                            .acknowledged(false)
                            .viewed(false)
                            .build();

                    populateLocalizedAlertFields(alert, null, salle);
                    return alertMapper.toDTO(create(alert));
                });
    }

    public AlertResponseDTO acknowledge(UUID alertId, User user, String ipAddress) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        if (Boolean.TRUE.equals(alert.getAcknowledged()) && Boolean.FALSE.equals(alert.getActive())) {
            return alertMapper.toDTO(alert);
        }

        alert.setAcknowledged(true);
        alert.setActive(false);
        alert.setAcknowledgedAt(LocalDateTime.now());
        alert.setAcknowledgedBy(user);

        Alert saved = alertRepository.save(alert);

        auditLogService.log(
                user,
                "ALERT_ACKNOWLEDGED",
                "ALERT",
                saved.getAlertId(),
                "type=" + saved.getType() + " | title=" + saved.getTitle(),
                ipAddress
        );

        messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(saved));

        return alertMapper.toDTO(saved);
    }

    public AlertResponseDTO markAsRead(UUID alertId, User user, String ipAddress) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        if (Boolean.TRUE.equals(alert.getViewed())) {
            return alertMapper.toDTO(alert);
        }

        alert.setViewed(true);
        alert.setViewedAt(LocalDateTime.now());

        Alert saved = alertRepository.save(alert);

        auditLogService.log(
                user,
                "ALERT_VIEWED",
                "ALERT",
                saved.getAlertId(),
                "type=" + saved.getType() + " | title=" + saved.getTitle(),
                ipAddress
        );

        messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(saved));

        return alertMapper.toDTO(saved);
    }

    public long markAllAsRead(User user, String ipAddress) {
        List<Alert> unreadAlerts = alertRepository.findByViewedFalseOrderByCreatedAtDesc();

        if (unreadAlerts.isEmpty()) {
            return 0L;
        }

        LocalDateTime now = LocalDateTime.now();

        unreadAlerts.forEach(alert -> {
            alert.setViewed(true);
            if (alert.getViewedAt() == null) {
                alert.setViewedAt(now);
            }
        });

        List<Alert> savedAlerts = alertRepository.saveAll(unreadAlerts);

        for (Alert alert : savedAlerts) {
            auditLogService.log(
                    user,
                    "ALERT_READ_ALL",
                    "ALERT",
                    alert.getAlertId(),
                    "type=" + alert.getType() + " | title=" + alert.getTitle(),
                    ipAddress
            );

            messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(alert));
        }

        return savedAlerts.size();
    }

    @Transactional(readOnly = true)
    public List<AlertResponseDTO> findActiveAlerts() {
        return alertRepository.findByActiveTrueOrderByCreatedAtDesc()
                .stream()
                .map(alertMapper::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<AlertResponseDTO> findAlertHistory(Pageable pageable) {
        return alertRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(alertMapper::toDTO);
    }

    private void validateIntervention(Intervention intervention) {
        if (intervention == null || intervention.getInterventionId() == null) {
            throw new IllegalArgumentException("Intervention is required");
        }
    }

    private void closeActiveInterventionAlerts(UUID interventionId, List<AlertType> types) {
        List<Alert> activeAlerts = alertRepository.findByIntervention_InterventionIdAndActiveTrue(interventionId);

        activeAlerts.stream()
                .filter(alert -> types.contains(alert.getType()))
                .forEach(alert -> alert.setActive(false));
    }

    private void closeActiveSalleAlerts(UUID salleId, List<AlertType> types) {
        List<Alert> activeAlerts = alertRepository.findBySalle_SalleIdAndActiveTrue(salleId);

        activeAlerts.stream()
                .filter(alert -> types.contains(alert.getType()))
                .forEach(alert -> alert.setActive(false));
    }

    private String buildInterventionMessage(Intervention intervention, String verb) {
        String interventionName = safe(intervention.getNomIntervention(), "Intervention");
        String patientName = buildPatientLabel(intervention);

        return interventionName + " pour " + patientName + " " + verb + ".";
    }

    private String buildInterventionMessageEn(Intervention intervention, String verb) {
        String interventionName = resolveInterventionLabelEn(intervention);
        String patientName = buildPatientLabel(intervention);

        return interventionName + " for " + patientName + " " + verb + ".";
    }

    private String buildInterventionMessageAr(Intervention intervention, String verb) {
        String interventionName = resolveInterventionLabelAr(intervention);
        String patientName = buildPatientLabelAr(intervention);

        return interventionName + " " + patientName + " " + verb + ".";
    }

    private String buildInterventionRescheduledMessage(
            Intervention intervention,
            String previousSlot,
            String nextSlot
    ) {
        String interventionName = safe(intervention.getNomIntervention(), "Intervention");
        String patientName = buildPatientLabel(intervention);
        String safePreviousSlot = safe(previousSlot, "ancien creneau inconnu");
        String safeNextSlot = safe(nextSlot, "nouveau creneau inconnu");

        return interventionName
                + " pour "
                + patientName
                + " a ete reprogrammee de "
                + safePreviousSlot
                + " vers "
                + safeNextSlot
                + ".";
    }

    private String buildInterventionRescheduledMessageEn(
            Intervention intervention,
            String previousSlot,
            String nextSlot
    ) {
        String interventionName = resolveInterventionLabelEn(intervention);
        String patientName = buildPatientLabel(intervention);
        String safePreviousSlot = safe(previousSlot, "unknown previous slot");
        String safeNextSlot = safe(nextSlot, "unknown next slot");

        return interventionName
                + " for "
                + patientName
                + " has been rescheduled from "
                + safePreviousSlot
                + " to "
                + safeNextSlot
                + ".";
    }

    private String buildInterventionRescheduledMessageAr(
            Intervention intervention,
            String previousSlot,
            String nextSlot
    ) {
        String interventionName = resolveInterventionLabelAr(intervention);
        String patientName = buildPatientLabelAr(intervention);
        String safePreviousSlot = safe(previousSlot, "موعد سابق غير معروف");
        String safeNextSlot = safe(nextSlot, "موعد جديد غير معروف");

        return "تمت إعادة جدولة "
                + interventionName
                + " للمريض "
                + patientName
                + " من "
                + safePreviousSlot
                + " إلى "
                + safeNextSlot
                + ".";
    }

    private String buildSspiOverrunMessage(SSPI sspi, Intervention intervention) {
        String patientName = buildPatientLabel(intervention);
        String interventionName = safe(intervention.getNomIntervention(), "Intervention");
        String startedAt = sspi.getHeureEntree() != null ? sspi.getHeureEntree().toString() : "inconnue";

        return "Le patient " + patientName
                + " a depasse le delai SSPI autorise apres "
                + interventionName
                + " (entree SSPI: " + startedAt + ").";
    }

    private String buildSspiOverrunMessageEn(SSPI sspi, Intervention intervention) {
        String patientName = buildPatientLabel(intervention);
        String interventionName = resolveInterventionLabelEn(intervention);
        String startedAt = sspi.getHeureEntree() != null ? sspi.getHeureEntree().toString() : "unknown";

        return "Patient " + patientName
                + " exceeded the authorized PACU delay after "
                + interventionName
                + " (PACU admission: " + startedAt + ").";
    }

    private String buildSspiOverrunMessageAr(SSPI sspi, Intervention intervention) {
        String patientName = buildPatientLabelAr(intervention);
        String interventionName = resolveInterventionLabelAr(intervention);
        String startedAt = sspi.getHeureEntree() != null ? sspi.getHeureEntree().toString() : "غير معروف";

        return "تجاوز المريض "
                + patientName
                + " المدة المسموح بها في الإفاقة بعد "
                + interventionName
                + " (وقت الدخول: "
                + startedAt
                + ").";
    }

    private String buildSalleStatusMessage(Salle salle) {
        String salleName = safe(salle.getNom(), "Salle");
        String state = Boolean.TRUE.equals(salle.getActive()) ? "est maintenant active" : "est maintenant inactive";

        return salleName + " " + state + ".";
    }

    private String buildSalleStatusMessageEn(Salle salle) {
        String salleName = resolveRoomLabelEn(salle);
        String state = Boolean.TRUE.equals(salle.getActive()) ? "is now active" : "is now inactive";

        return salleName + " " + state + ".";
    }

    private String buildSalleStatusMessageAr(Salle salle) {
        String salleName = resolveRoomLabelAr(salle);
        String state = Boolean.TRUE.equals(salle.getActive()) ? "أصبحت مفعلة الآن" : "أصبحت غير مفعلة الآن";

        return salleName + " " + state + ".";
    }

    private String buildPatientLabel(Intervention intervention) {
        if (intervention.getPatient() == null) {
            return "patient inconnu";
        }

        String prenom = intervention.getPatient().getPrenom() != null
                ? intervention.getPatient().getPrenom().trim()
                : "";

        String nom = intervention.getPatient().getNom() != null
                ? intervention.getPatient().getNom().trim()
                : "";

        String fullName = (prenom + " " + nom).trim();

        return fullName.isBlank() ? "patient inconnu" : fullName;
    }

    private String buildPatientLabelAr(Intervention intervention) {
        return buildPatientLabel(intervention);
    }

    private String resolveInterventionLabelEn(Intervention intervention) {
        if (intervention == null) {
            return "Procedure";
        }

        if (intervention.getNomInterventionEn() != null && !intervention.getNomInterventionEn().isBlank()) {
            return intervention.getNomInterventionEn().trim();
        }

        if (intervention.getCatalog() != null
                && intervention.getCatalog().getDesignationEn() != null
                && !intervention.getCatalog().getDesignationEn().isBlank()) {
            return intervention.getCatalog().getDesignationEn().trim();
        }

        return safe(intervention.getNomIntervention(), "Procedure");
    }

    private String resolveInterventionLabelAr(Intervention intervention) {
        if (intervention == null) {
            return "عملية";
        }

        if (intervention.getNomInterventionAr() != null && !intervention.getNomInterventionAr().isBlank()) {
            return intervention.getNomInterventionAr().trim();
        }

        if (intervention.getCatalog() != null
                && intervention.getCatalog().getDesignationAr() != null
                && !intervention.getCatalog().getDesignationAr().isBlank()) {
            return intervention.getCatalog().getDesignationAr().trim();
        }

        return safe(intervention.getNomIntervention(), "عملية");
    }

    private String resolveRoomLabelEn(Salle salle) {
        if (salle == null) {
            return "Room";
        }

        if (salle.getNomEn() != null && !salle.getNomEn().isBlank()) {
            return salle.getNomEn().trim();
        }

        return safe(salle.getNom(), "Room");
    }

    private String resolveRoomLabelAr(Salle salle) {
        if (salle == null) {
            return "غرفة عمليات";
        }

        if (salle.getNomAr() != null && !salle.getNomAr().isBlank()) {
            return salle.getNomAr().trim();
        }

        return safe(salle.getNom(), "غرفة عمليات");
    }

    private void populateLocalizedAlertFields(Alert alert, Intervention intervention, Salle salle) {
        alert.setPatientLabel(intervention != null ? buildPatientLabel(intervention) : null);
        alert.setPatientLabelEn(intervention != null ? buildPatientLabel(intervention) : null);
        alert.setPatientLabelAr(intervention != null ? buildPatientLabelAr(intervention) : null);

        alert.setInterventionLabel(intervention != null ? safe(intervention.getNomIntervention(), "Intervention") : null);
        alert.setInterventionLabelEn(intervention != null ? resolveInterventionLabelEn(intervention) : null);
        alert.setInterventionLabelAr(intervention != null ? resolveInterventionLabelAr(intervention) : null);

        Salle effectiveSalle = salle != null ? salle : intervention != null ? intervention.getSalle() : null;

        alert.setRoomLabel(effectiveSalle != null ? safe(effectiveSalle.getNom(), "Salle") : null);
        alert.setRoomLabelEn(effectiveSalle != null ? resolveRoomLabelEn(effectiveSalle) : null);
        alert.setRoomLabelAr(effectiveSalle != null ? resolveRoomLabelAr(effectiveSalle) : null);
    }

    private String safe(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public void resolveSspiOverrunAlerts(UUID interventionId) {
        List<Alert> alerts = alertRepository.findAllByTypeAndIntervention_InterventionIdAndActiveTrue(
                AlertType.SSPI_DEPASSEMENT,
                interventionId
        );

        if (alerts.isEmpty()) {
            return;
        }

        for (Alert alert : alerts) {
            alert.setActive(false);
            Alert saved = alertRepository.save(alert);
            messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(saved));
        }
    }

    public void resolveSspiAutomaticVitalAlerts(UUID interventionId, AlertType type) {
        List<Alert> alerts = alertRepository.findAllByTypeAndIntervention_InterventionIdAndActiveTrue(
                type,
                interventionId
        );

        if (alerts.isEmpty()) {
            return;
        }

        for (Alert alert : alerts) {
            alert.setActive(false);
            Alert saved = alertRepository.save(alert);
            messagingTemplate.convertAndSend("/topic/alerts", alertMapper.toDTO(saved));
        }
    }
}
