package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.InterventionCatalog;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import com.tn.softsys.blocoperatoire.domain.TempsOperatoire;
import com.tn.softsys.blocoperatoire.domain.oms.ChecklistOms;
import com.tn.softsys.blocoperatoire.dto.quality.QualityOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.repository.ChecklistOmsRepository;
import com.tn.softsys.blocoperatoire.repository.IncidentSSPIRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.SalleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class QualityService {

    private static final int QUALITY_WINDOW_DAYS = 30;
    private static final int MAX_ALLERGY_SLICES = 5;
    private static final int MAX_ALERTS = 8;

    private final InterventionRepository interventionRepository;
    private final ChecklistOmsRepository checklistOmsRepository;
    private final SalleRepository salleRepository;
    private final IncidentSSPIRepository incidentSSPIRepository;

    public QualityOverviewResponseDTO getOverview(LocalDate referenceDate, String blocFilter, String specialtyFilter) {
        LocalDate day = referenceDate != null ? referenceDate : interventionRepository.findTopByOrderByDateInterventionDesc()
                .map(Intervention::getDateIntervention)
                .orElse(LocalDate.now());
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEndInclusive = day.plusDays(1).atStartOfDay().minusNanos(1);
        LocalDateTime qualityStart = dayStart.minusDays(QUALITY_WINDOW_DAYS - 1L);

        String normalizedBloc = normalizeFilter(blocFilter);
        String normalizedSpecialty = normalizeFilter(specialtyFilter);

        List<Intervention> interventionsToday = interventionRepository.findByDateIntervention(day);
        List<Intervention> allInterventions = interventionRepository.findAll();
        List<ChecklistOms> checklistsToday = checklistOmsRepository.findByIntervention_DateIntervention(day);
        List<Salle> allRooms = salleRepository.findAll();
        List<IncidentSSPI> qualityIncidents = incidentSSPIRepository
                .findByDeclaredAtBetweenOrderByDeclaredAtDesc(qualityStart, dayEndInclusive);

        List<Intervention> auditableInterventions = interventionsToday.stream()
                .filter(item -> item.getStatut() != StatutIntervention.ANNULEE)
                .toList();

        List<String> availableBlocs = allRooms.stream()
                .map(Salle::getIdBloc)
                .map(this::safeTrim)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<String> availableSpecialties = allInterventions.stream()
                .map(this::extractSpecialtyLabel)
                .filter(value -> !value.isBlank())
                .distinct()
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();

        List<LocalDate> availableDates = allInterventions.stream()
                .map(Intervention::getDateIntervention)
                .filter(Objects::nonNull)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .limit(18)
                .toList();

        Predicate<Intervention> interventionScope = intervention ->
                matchesBloc(intervention, normalizedBloc) && matchesSpecialty(intervention, normalizedSpecialty);

        List<Intervention> filteredInterventions = auditableInterventions.stream()
                .filter(interventionScope)
                .toList();

        Set<UUID> filteredInterventionIds = filteredInterventions.stream()
                .map(Intervention::getInterventionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        Map<UUID, ChecklistOms> checklistByInterventionId = checklistsToday.stream()
                .filter(item -> item.getIntervention() != null && item.getIntervention().getInterventionId() != null)
                .collect(Collectors.toMap(
                        item -> item.getIntervention().getInterventionId(),
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        List<ChecklistOms> relevantChecklists = filteredInterventionIds.isEmpty()
                ? List.of()
                : checklistsToday.stream()
                .filter(item -> item.getIntervention() != null && filteredInterventionIds.contains(item.getIntervention().getInterventionId()))
                .toList();

        List<Patient> scopedPatients = filteredInterventions.stream()
                .map(Intervention::getPatient)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Salle> scopedRooms = allRooms.stream()
                .filter(room -> normalizedBloc.isBlank() || safeTrim(room.getIdBloc()).equalsIgnoreCase(normalizedBloc))
                .toList();

        List<IncidentSSPI> relevantIncidents = qualityIncidents.stream()
                .filter(item -> {
                    Intervention intervention = resolveIntervention(item);
                    return intervention != null && interventionScope.test(intervention);
                })
                .toList();

        int signInCompleted = (int) relevantChecklists.stream().filter(item -> item.getSignIn() != null).count();
        int timeOutCompleted = (int) relevantChecklists.stream().filter(item -> item.getTimeOut() != null).count();
        int signOutCompleted = (int) relevantChecklists.stream().filter(item -> item.getSignOut() != null).count();
        int fullyCompleted = (int) filteredInterventions.stream()
                .filter(item -> isChecklistComplete(item, checklistByInterventionId))
                .count();
        int outstandingCount = Math.max(0, filteredInterventions.size() - fullyCompleted);
        int complianceRate = computeRate(fullyCompleted, filteredInterventions.size(), 0);

        List<QualityOverviewResponseDTO.FlowMetricSnapshot> flowMetrics = List.of(
                buildFlowMetric("entry_to_anesthesia", "Entree -> debut anesthesie", filteredInterventions, checklistByInterventionId, this::resolveEntryBloc, this::resolveAnesthesiaStart),
                buildFlowMetric("entry_to_incision", "Entree -> incision", filteredInterventions, checklistByInterventionId, this::resolveEntryBloc, this::resolveIncision),
                buildFlowMetric("incision_to_closure", "Incision -> fin acte", filteredInterventions, checklistByInterventionId, this::resolveIncision, this::resolveEndOfSurgery),
                buildFlowMetric("entry_to_exit", "Entree -> sortie salle", filteredInterventions, checklistByInterventionId, this::resolveEntryBloc, this::resolveExitRoom)
        );
        int sampledInterventions = flowMetrics.stream().mapToInt(QualityOverviewResponseDTO.FlowMetricSnapshot::sampleCount).max().orElse(0);
        int averageEntryToIncisionMinutes = flowMetrics.stream()
                .filter(item -> "entry_to_incision".equals(item.key()))
                .findFirst()
                .map(QualityOverviewResponseDTO.FlowMetricSnapshot::averageMinutes)
                .orElse(0);

        AllergyDistribution allergyDistribution = buildAllergyDistribution(scopedPatients);
        RoomDistribution roomDistribution = buildRoomDistribution(scopedRooms);
        IncidentDistribution incidentDistribution = buildIncidentDistribution(relevantIncidents);
        List<QualityOverviewResponseDTO.QualityAlertSnapshot> alerts = buildQualityAlerts(
                filteredInterventions,
                checklistByInterventionId,
                scopedRooms,
                relevantIncidents
        );

        return new QualityOverviewResponseDTO(
                day,
                LocalDateTime.now(),
                new QualityOverviewResponseDTO.FilterSnapshot(
                        normalizedBloc,
                        normalizedSpecialty,
                        availableBlocs,
                        availableSpecialties,
                        availableDates
                ),
                new QualityOverviewResponseDTO.TraceabilitySnapshot(
                        filteredInterventions.size(),
                        relevantChecklists.size(),
                        relevantIncidents.size(),
                        countOperativeTimelineSources(filteredInterventions),
                        countOmsTimelineFallbackSources(filteredInterventions, checklistByInterventionId),
                        countScheduledTimelineSources(filteredInterventions, checklistByInterventionId)
                ),
                new QualityOverviewResponseDTO.SummarySnapshot(
                        fullyCompleted,
                        complianceRate,
                        allergyDistribution.allergicPatients(),
                        averageEntryToIncisionMinutes,
                        alerts.size()
                ),
                new QualityOverviewResponseDTO.ChecklistSnapshot(
                        filteredInterventions.size(),
                        signInCompleted,
                        timeOutCompleted,
                        signOutCompleted,
                        fullyCompleted,
                        outstandingCount,
                        complianceRate
                ),
                new QualityOverviewResponseDTO.PatientFlowSnapshot(
                        sampledInterventions,
                        filteredInterventions.size(),
                        flowMetrics
                ),
                new QualityOverviewResponseDTO.AllergySnapshot(
                        scopedPatients.size(),
                        allergyDistribution.allergicPatients(),
                        Math.max(0, scopedPatients.size() - allergyDistribution.allergicPatients()),
                        allergyDistribution.totalAllergyEntries(),
                        allergyDistribution.slices()
                ),
                new QualityOverviewResponseDTO.RoomDistributionSnapshot(
                        scopedRooms.size(),
                        roomDistribution.operationalRooms(),
                        Math.max(0, scopedRooms.size() - roomDistribution.operationalRooms()),
                        roomDistribution.types()
                ),
                new QualityOverviewResponseDTO.IncidentSnapshot(
                        incidentDistribution.totalIncidents(),
                        incidentDistribution.openIncidents(),
                        incidentDistribution.criticalIncidents(),
                        incidentDistribution.nearMisses(),
                        incidentDistribution.resolutionRate(),
                        incidentDistribution.recent()
                ),
                alerts
        );
    }

    private QualityOverviewResponseDTO.FlowMetricSnapshot buildFlowMetric(
            String key,
            String label,
            List<Intervention> interventions,
            Map<UUID, ChecklistOms> checklistByInterventionId,
            FlowTimeExtractor startExtractor,
            FlowTimeExtractor endExtractor
    ) {
        List<Long> durations = new ArrayList<>();
        int operativeSourceCount = 0;
        int omsFallbackSourceCount = 0;
        int scheduledSourceCount = 0;
        int missingSourceCount = 0;

        for (Intervention intervention : interventions) {
            ChecklistOms checklist = intervention.getInterventionId() != null
                    ? checklistByInterventionId.get(intervention.getInterventionId())
                    : null;
            TimestampEvidence start = startExtractor.extract(intervention, checklist);
            TimestampEvidence end = endExtractor.extract(intervention, checklist);
            if (start.value() == null || end.value() == null || end.value().isBefore(start.value())) {
                missingSourceCount += 1;
                continue;
            }

            TimelineSource pairSource = weakestSource(start.source(), end.source());
            switch (pairSource) {
                case OPERATIVE -> operativeSourceCount += 1;
                case OMS_FALLBACK -> omsFallbackSourceCount += 1;
                case SCHEDULED -> scheduledSourceCount += 1;
                default -> missingSourceCount += 1;
            }

            durations.add(Duration.between(start.value(), end.value()).toMinutes());
        }

        int sampleCount = durations.size();
        int average = sampleCount == 0
                ? 0
                : (int) Math.round(durations.stream().mapToLong(Long::longValue).average().orElse(0.0));
        int coverageRate = computeRate(sampleCount, interventions.size(), 0);

        return new QualityOverviewResponseDTO.FlowMetricSnapshot(
                key,
                label,
                average,
                sampleCount,
                operativeSourceCount,
                omsFallbackSourceCount,
                scheduledSourceCount,
                Math.max(missingSourceCount, interventions.size() - sampleCount),
                coverageRate,
                resolveReliability(sampleCount, operativeSourceCount, omsFallbackSourceCount, scheduledSourceCount, coverageRate)
        );
    }

    private AllergyDistribution buildAllergyDistribution(List<Patient> patients) {
        Map<String, Integer> counts = new LinkedHashMap<>();
        int allergicPatients = 0;
        int totalEntries = 0;

        for (Patient patient : patients) {
            List<String> normalized = patient.getAllergies().stream()
                    .map(this::normalizeAllergyLabel)
                    .filter(label -> !label.isBlank())
                    .distinct()
                    .toList();

            if (!normalized.isEmpty()) {
                allergicPatients += 1;
            }

            for (String label : normalized) {
                counts.merge(label, 1, Integer::sum);
                totalEntries += 1;
            }
        }

        List<Map.Entry<String, Integer>> ordered = counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .toList();

        List<QualityOverviewResponseDTO.AllergySliceSnapshot> slices = new ArrayList<>();
        int otherCount = 0;

        for (int index = 0; index < ordered.size(); index++) {
            Map.Entry<String, Integer> entry = ordered.get(index);
            if (index < MAX_ALLERGY_SLICES) {
                slices.add(new QualityOverviewResponseDTO.AllergySliceSnapshot(
                        entry.getKey(),
                        slugify(entry.getKey()),
                        entry.getValue(),
                        computeRate(entry.getValue(), totalEntries, 0)
                ));
            } else {
                otherCount += entry.getValue();
            }
        }

        if (otherCount > 0) {
            slices.add(new QualityOverviewResponseDTO.AllergySliceSnapshot(
                    "Autres",
                    "AUTRES",
                    otherCount,
                    computeRate(otherCount, totalEntries, 0)
            ));
        }

        return new AllergyDistribution(allergicPatients, totalEntries, slices);
    }

    private RoomDistribution buildRoomDistribution(List<Salle> rooms) {
        Map<String, List<Salle>> byType = rooms.stream()
                .collect(Collectors.groupingBy(
                        this::extractRoomTypeLabel,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));

        List<QualityOverviewResponseDTO.RoomTypeSnapshot> types = byType.entrySet().stream()
                .sorted((left, right) -> Integer.compare(right.getValue().size(), left.getValue().size()))
                .map(entry -> new QualityOverviewResponseDTO.RoomTypeSnapshot(
                        entry.getKey(),
                        slugify(entry.getKey()),
                        entry.getValue().size(),
                        (int) entry.getValue().stream().filter(this::isOperationalRoom).count()
                ))
                .toList();

        int operationalRooms = (int) rooms.stream().filter(this::isOperationalRoom).count();
        return new RoomDistribution(operationalRooms, types);
    }

    private IncidentDistribution buildIncidentDistribution(List<IncidentSSPI> incidents) {
        List<QualityOverviewResponseDTO.IncidentItemSnapshot> recent = incidents.stream()
                .limit(6)
                .map(this::toIncidentItem)
                .toList();

        int openIncidents = (int) incidents.stream().filter(item -> !Boolean.TRUE.equals(item.getResolu())).count();
        int criticalIncidents = (int) incidents.stream()
                .filter(item -> {
                    String severity = normalizeIncidentSeverity(item.getGravite());
                    return "critical".equals(severity) || "severe".equals(severity);
                })
                .count();
        int nearMisses = (int) incidents.stream()
                .filter(item -> "minor".equals(normalizeIncidentSeverity(item.getGravite())))
                .count();
        int resolutionRate = computeRate(
                (int) incidents.stream().filter(item -> Boolean.TRUE.equals(item.getResolu())).count(),
                incidents.size(),
                100
        );

        return new IncidentDistribution(
                incidents.size(),
                openIncidents,
                criticalIncidents,
                nearMisses,
                resolutionRate,
                recent
        );
    }

    private List<QualityOverviewResponseDTO.QualityAlertSnapshot> buildQualityAlerts(
            List<Intervention> interventions,
            Map<UUID, ChecklistOms> checklistByInterventionId,
            List<Salle> rooms,
            List<IncidentSSPI> incidents
    ) {
        List<QualityOverviewResponseDTO.QualityAlertSnapshot> alerts = new ArrayList<>();

        for (Intervention intervention : interventions) {
            if (isChecklistComplete(intervention, checklistByInterventionId)) {
                continue;
            }

            ChecklistOms checklist = checklistByInterventionId.get(intervention.getInterventionId());
            alerts.add(new QualityOverviewResponseDTO.QualityAlertSnapshot(
                    "checklist_gap",
                    "warning",
                    "Checklist OMS incomplete",
                    "Etapes manquantes: " + String.join(", ", resolveMissingChecklistStages(checklist)),
                    safe(intervention.getNomIntervention(), "Intervention") + " - " + formatPatientLabel(intervention.getPatient()),
                    combine(intervention.getDateIntervention(), intervention.getHeureDebut())
            ));
        }

        Map<UUID, List<Intervention>> interventionsByRoom = interventions.stream()
                .filter(item -> item.getSalle() != null && item.getSalle().getSalleId() != null)
                .collect(Collectors.groupingBy(item -> item.getSalle().getSalleId()));

        for (Salle room : rooms) {
            if (isOperationalRoom(room)) {
                continue;
            }

            long scheduledCount = interventionsByRoom.getOrDefault(room.getSalleId(), List.of()).stream()
                    .filter(item -> item.getStatut() == StatutIntervention.PLANIFIEE || item.getStatut() == StatutIntervention.EN_COURS)
                    .count();

            if (scheduledCount <= 0L) {
                continue;
            }

            alerts.add(new QualityOverviewResponseDTO.QualityAlertSnapshot(
                    "room_conflict",
                    "critical",
                    "Conflit de salle indisponible",
                    scheduledCount + " intervention(s) assignee(s) a une salle non operationnelle",
                    safe(room.getNom(), "Salle"),
                    LocalDateTime.now()
            ));
        }

        incidents.stream()
                .filter(item -> !Boolean.TRUE.equals(item.getResolu()))
                .forEach(item -> alerts.add(new QualityOverviewResponseDTO.QualityAlertSnapshot(
                        "incident",
                        mapIncidentSeverityToAlertSeverity(item.getGravite()),
                        "Incident qualite ouvert",
                        prettify(item.getType()) + " - " + safe(item.getDescription(), "Suivi requis"),
                        formatIncidentReference(item),
                        item.getDeclaredAt()
                )));

        return alerts.stream()
                .sorted(Comparator
                        .comparingInt((QualityOverviewResponseDTO.QualityAlertSnapshot item) -> alertSeverityRank(item.severity()))
                        .thenComparing(QualityOverviewResponseDTO.QualityAlertSnapshot::occurredAt, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(MAX_ALERTS)
                .toList();
    }

    private QualityOverviewResponseDTO.IncidentItemSnapshot toIncidentItem(IncidentSSPI incident) {
        Intervention intervention = resolveIntervention(incident);

        return new QualityOverviewResponseDTO.IncidentItemSnapshot(
                incident.getIncidentId(),
                formatPatientLabel(resolvePatient(incident)),
                safe(intervention != null ? intervention.getNomIntervention() : null, "Intervention"),
                prettify(incident.getType()),
                normalizeIncidentSeverity(incident.getGravite()),
                Boolean.TRUE.equals(incident.getResolu()),
                incident.getDeclaredAt()
        );
    }

    private boolean matchesBloc(Intervention intervention, String normalizedBloc) {
        if (normalizedBloc.isBlank()) {
            return true;
        }

        Salle room = intervention.getSalle();
        return room != null && safeTrim(room.getIdBloc()).equalsIgnoreCase(normalizedBloc);
    }

    private boolean matchesSpecialty(Intervention intervention, String normalizedSpecialty) {
        if (normalizedSpecialty.isBlank()) {
            return true;
        }

        return extractSpecialtyLabel(intervention).equalsIgnoreCase(normalizedSpecialty);
    }

    private Patient resolvePatient(IncidentSSPI incident) {
        Intervention intervention = resolveIntervention(incident);
        return intervention != null ? intervention.getPatient() : null;
    }

    private Intervention resolveIntervention(IncidentSSPI incident) {
        return incident.getSspi() != null ? incident.getSspi().getIntervention() : null;
    }

    private boolean isChecklistComplete(Intervention intervention, Map<UUID, ChecklistOms> checklistByInterventionId) {
        ChecklistOms checklist = checklistByInterventionId.get(intervention.getInterventionId());
        return checklist != null
                && checklist.getSignIn() != null
                && checklist.getTimeOut() != null
                && checklist.getSignOut() != null;
    }

    private List<String> resolveMissingChecklistStages(ChecklistOms checklist) {
        List<String> missing = new ArrayList<>();
        if (checklist == null || checklist.getSignIn() == null) missing.add("Sign In");
        if (checklist == null || checklist.getTimeOut() == null) missing.add("Time Out");
        if (checklist == null || checklist.getSignOut() == null) missing.add("Sign Out");
        return missing;
    }

    private TimestampEvidence resolveEntryBloc(Intervention intervention, ChecklistOms checklist) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getEntreeBloc() != null) {
            return new TimestampEvidence(temps.getEntreeBloc(), TimelineSource.OPERATIVE);
        }
        LocalDateTime scheduled = combine(intervention.getDateIntervention(), intervention.getHeureDebut());
        if (scheduled != null) {
            return new TimestampEvidence(scheduled, TimelineSource.SCHEDULED);
        }
        return TimestampEvidence.missing();
    }

    private TimestampEvidence resolveAnesthesiaStart(Intervention intervention, ChecklistOms checklist) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getDebutAnesthesie() != null) {
            return new TimestampEvidence(temps.getDebutAnesthesie(), TimelineSource.OPERATIVE);
        }
        if (checklist != null && checklist.getSignIn() != null && checklist.getSignIn().getCompletedAt() != null) {
            return new TimestampEvidence(checklist.getSignIn().getCompletedAt(), TimelineSource.OMS_FALLBACK);
        }
        return TimestampEvidence.missing();
    }

    private TimestampEvidence resolveIncision(Intervention intervention, ChecklistOms checklist) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getIncision() != null) {
            return new TimestampEvidence(temps.getIncision(), TimelineSource.OPERATIVE);
        }
        if (checklist != null && checklist.getTimeOut() != null && checklist.getTimeOut().getCompletedAt() != null) {
            return new TimestampEvidence(checklist.getTimeOut().getCompletedAt(), TimelineSource.OMS_FALLBACK);
        }
        return TimestampEvidence.missing();
    }

    private TimestampEvidence resolveEndOfSurgery(Intervention intervention, ChecklistOms checklist) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getFinActe() != null) {
            return new TimestampEvidence(temps.getFinActe(), TimelineSource.OPERATIVE);
        }
        if (checklist != null && checklist.getSignOut() != null && checklist.getSignOut().getCompletedAt() != null) {
            return new TimestampEvidence(checklist.getSignOut().getCompletedAt(), TimelineSource.OMS_FALLBACK);
        }
        return TimestampEvidence.missing();
    }

    private TimestampEvidence resolveExitRoom(Intervention intervention, ChecklistOms checklist) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getSortieSalle() != null) {
            return new TimestampEvidence(temps.getSortieSalle(), TimelineSource.OPERATIVE);
        }
        if (checklist != null && checklist.getSignOut() != null && checklist.getSignOut().getCompletedAt() != null) {
            return new TimestampEvidence(checklist.getSignOut().getCompletedAt(), TimelineSource.OMS_FALLBACK);
        }
        return TimestampEvidence.missing();
    }

    private boolean isOperationalRoom(Salle salle) {
        if (salle == null) {
            return false;
        }
        if (salle.getStatut() != null) {
            return salle.getStatut().isOperational();
        }
        return Boolean.TRUE.equals(salle.getActive());
    }

    private String extractRoomTypeLabel(Salle salle) {
        String raw = safeTrim(salle != null ? salle.getNom() : null);
        if (raw.contains("-")) {
            return prettify(raw.substring(raw.indexOf('-') + 1));
        }
        if (!safeTrim(salle != null ? salle.getIdBloc() : null).isBlank()) {
            return prettify(salle.getIdBloc());
        }
        return "Salle generale";
    }

    private String extractSpecialtyLabel(Intervention intervention) {
        return classifySpecialty(intervention);
    }

    private String classifySpecialty(Intervention intervention) {
        String source = buildSpecialtySource(intervention).toLowerCase(Locale.ROOT);

        if (containsAny(source, "orthop", "fracture", "traumato")) return "Orthopedie";
        if (containsAny(source, "urolog", "prostate", "renale")) return "Urologie";
        if (containsAny(source, "gyne", "obst", "cesar", "uter")) return "Gynecologie obstetrique";
        if (containsAny(source, "neuro")) return "Neurochirurgie";
        if (containsAny(source, "card", "thorac")) return "Chirurgie cardiaque thoracique";
        if (containsAny(source, "orl")) return "ORL";
        if (containsAny(source, "opht")) return "Ophtalmologie";
        if (containsAny(source, "digest", "viscer", "abdom", "laparosc")) return "Chirurgie digestive";
        if (containsAny(source, "vascul")) return "Chirurgie vasculaire";
        if (containsAny(source, "plast", "reconstruct")) return "Chirurgie plastique";
        if (containsAny(source, "pedia", "enfant")) return "Chirurgie pediatrique";
        if (containsAny(source, "maxillo", "face")) return "Maxillo-faciale";
        if (containsAny(source, "polyval")) return "Polyvalente";
        return "Chirurgie generale";
    }

    private String buildSpecialtySource(Intervention intervention) {
        List<String> candidates = new ArrayList<>();
        InterventionCatalog catalog = intervention.getCatalog();
        if (catalog != null) {
            candidates.add(safeTrim(catalog.getDesignation()));
        }
        candidates.add(safeTrim(intervention.getNomIntervention()));
        candidates.add(safeTrim(intervention.getCodeActe()));
        Salle room = intervention.getSalle();
        if (room != null) {
            candidates.add(safeTrim(room.getNom()));
            candidates.add(safeTrim(room.getIdBloc()));
        }
        return String.join(" ", candidates);
    }

    private boolean containsAny(String source, String... keywords) {
        for (String keyword : keywords) {
            if (source.contains(keyword)) {
                return true;
            }
        }
        return false;
    }


    private int countOperativeTimelineSources(List<Intervention> interventions) {
        return (int) interventions.stream().filter(this::hasOperativeTimeline).count();
    }

    private int countOmsTimelineFallbackSources(List<Intervention> interventions, Map<UUID, ChecklistOms> checklistByInterventionId) {
        return (int) interventions.stream()
                .filter(intervention -> !hasOperativeTimeline(intervention))
                .filter(intervention -> {
                    ChecklistOms checklist = intervention.getInterventionId() != null
                            ? checklistByInterventionId.get(intervention.getInterventionId())
                            : null;
                    return hasOmsTimeline(checklist);
                })
                .count();
    }

    private int countScheduledTimelineSources(List<Intervention> interventions, Map<UUID, ChecklistOms> checklistByInterventionId) {
        return (int) interventions.stream()
                .filter(intervention -> !hasOperativeTimeline(intervention))
                .filter(intervention -> {
                    ChecklistOms checklist = intervention.getInterventionId() != null
                            ? checklistByInterventionId.get(intervention.getInterventionId())
                            : null;
                    return !hasOmsTimeline(checklist) && hasScheduledTimeline(intervention);
                })
                .count();
    }

    private boolean hasOperativeTimeline(Intervention intervention) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        return temps != null && (
                temps.getEntreeBloc() != null
                        || temps.getDebutAnesthesie() != null
                        || temps.getIncision() != null
                        || temps.getFinActe() != null
                        || temps.getSortieSalle() != null
        );
    }

    private boolean hasOmsTimeline(ChecklistOms checklist) {
        return checklist != null && (
                (checklist.getSignIn() != null && checklist.getSignIn().getCompletedAt() != null)
                        || (checklist.getTimeOut() != null && checklist.getTimeOut().getCompletedAt() != null)
                        || (checklist.getSignOut() != null && checklist.getSignOut().getCompletedAt() != null)
        );
    }

    private boolean hasScheduledTimeline(Intervention intervention) {
        return intervention != null && intervention.getDateIntervention() != null && intervention.getHeureDebut() != null;
    }

    private String normalizeAllergyLabel(String value) {
        String normalized = fold(value)
                .replace('_', ' ')
                .replace('-', ' ')
                .trim();

        if (normalized.isBlank()) {
            return "";
        }

        if (containsAny(normalized, "penicilline", "penicillin", "peniciline", "penicil")) return "Penicilline";
        if (containsAny(normalized, "latex", "caoutchouc")) return "Latex";
        if (containsAny(normalized, "iode", "iodine", "betadine", "povidone")) return "Iode";
        if (containsAny(normalized, "ains", "ibuprof", "ketopro", "diclofen", "aspir")) return "AINS / salicyles";
        if (containsAny(normalized, "opio", "morph", "tramadol")) return "Opioides";
        if (containsAny(normalized, "sulfamide", "sulfa")) return "Sulfamides";

        return prettify(normalized);
    }

    private TimelineSource weakestSource(TimelineSource left, TimelineSource right) {
        return left.rank() <= right.rank() ? left : right;
    }

    private String resolveReliability(int sampleCount, int operativeCount, int omsCount, int scheduledCount, int coverageRate) {
        if (sampleCount <= 0 || coverageRate < 25) {
            return "insufficient";
        }
        if (operativeCount * 100 >= sampleCount * 60 && coverageRate >= 60) {
            return "high";
        }
        if ((operativeCount + omsCount) * 100 >= sampleCount * 70) {
            return "medium";
        }
        if (scheduledCount > 0 || coverageRate < 60) {
            return "limited";
        }
        return "medium";
    }

    private String normalizeFilter(String value) {
        String normalized = safeTrim(value);
        if (normalized.equalsIgnoreCase("ALL")
                || normalized.equalsIgnoreCase("TOUS")
                || normalized.equalsIgnoreCase("TOUTES")
                || normalized.equalsIgnoreCase("TOUS LES BLOCS")
                || normalized.equalsIgnoreCase("TOUTES LES SPECIALITES")) {
            return "";
        }
        return normalized;
    }

    private String safeTrim(String value) {
        return value == null ? "" : value.trim();
    }

    private String slugify(String value) {
        return fold(value).replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "").toUpperCase(Locale.ROOT);
    }

    private String fold(String value) {
        String safeValue = safe(value, "");
        String withoutAccents = Normalizer.normalize(safeValue, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return withoutAccents.toLowerCase(Locale.ROOT);
    }

    private String formatPatientLabel(Patient patient) {
        if (patient == null) {
            return "Patient inconnu";
        }
        String fullName = (safe(patient.getPrenom(), "") + " " + safe(patient.getNom(), "")).trim();
        if (!safe(patient.getMrn(), "").isBlank()) {
            return fullName.isBlank() ? patient.getMrn() : fullName + " (" + patient.getMrn() + ")";
        }
        return fullName.isBlank() ? "Patient inconnu" : fullName;
    }

    private String mapIncidentSeverityToAlertSeverity(String value) {
        String normalized = normalizeIncidentSeverity(value);
        if ("critical".equals(normalized) || "severe".equals(normalized)) return "critical";
        if ("moderate".equals(normalized)) return "warning";
        return "info";
    }

    private String normalizeIncidentSeverity(String value) {
        String normalized = safe(value, "").trim().toUpperCase(Locale.ROOT);
        if (normalized.contains("CRIT")) return "critical";
        if (normalized.contains("SEVER") || normalized.contains("GRAVE")) return "severe";
        if (normalized.contains("MOD")) return "moderate";
        return "minor";
    }

    private int alertSeverityRank(String value) {
        if ("critical".equalsIgnoreCase(value)) return 0;
        if ("warning".equalsIgnoreCase(value)) return 1;
        return 2;
    }

    private String formatIncidentReference(IncidentSSPI incident) {
        Intervention intervention = resolveIntervention(incident);
        String interventionLabel = intervention != null ? safe(intervention.getNomIntervention(), "Intervention") : "Intervention";
        return interventionLabel + " - " + formatPatientLabel(resolvePatient(incident));
    }

    private LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null || time == null) return null;
        return LocalDateTime.of(date, time);
    }

    private int computeRate(int numerator, int denominator, int fallback) {
        if (denominator <= 0) return fallback;
        double value = (double) numerator * 100.0 / (double) denominator;
        return (int) Math.max(0, Math.min(100, Math.round(value)));
    }

    private String prettify(String value) {
        String normalized = safe(value, "Non precise")
                .trim()
                .replace('_', ' ')
                .replace('-', ' ')
                .toLowerCase(Locale.ROOT);

        if (normalized.isBlank()) {
            return "Non precise";
        }

        return java.util.Arrays.stream(normalized.split("\\s+"))
                .filter(part -> !part.isBlank())
                .map(part -> Character.toUpperCase(part.charAt(0)) + part.substring(1))
                .collect(Collectors.joining(" "));
    }

    private String safe(String value, String fallback) {
        return value != null ? value : fallback;
    }

    @FunctionalInterface
    private interface FlowTimeExtractor {
        TimestampEvidence extract(Intervention intervention, ChecklistOms checklist);
    }

    private enum TimelineSource {
        OPERATIVE(3),
        OMS_FALLBACK(2),
        SCHEDULED(1),
        MISSING(0);

        private final int rank;

        TimelineSource(int rank) {
            this.rank = rank;
        }

        public int rank() {
            return rank;
        }
    }

    private record TimestampEvidence(LocalDateTime value, TimelineSource source) {
        private static TimestampEvidence missing() {
            return new TimestampEvidence(null, TimelineSource.MISSING);
        }
    }

    private record AllergyDistribution(
            int allergicPatients,
            int totalAllergyEntries,
            List<QualityOverviewResponseDTO.AllergySliceSnapshot> slices
    ) {}

    private record RoomDistribution(
            int operationalRooms,
            List<QualityOverviewResponseDTO.RoomTypeSnapshot> types
    ) {}

    private record IncidentDistribution(
            int totalIncidents,
            int openIncidents,
            int criticalIncidents,
            int nearMisses,
            int resolutionRate,
            List<QualityOverviewResponseDTO.IncidentItemSnapshot> recent
    ) {}
}
