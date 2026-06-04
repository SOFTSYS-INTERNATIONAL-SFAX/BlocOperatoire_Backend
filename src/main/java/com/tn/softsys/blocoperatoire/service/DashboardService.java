package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.IncidentSSPI;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.Salle;
import com.tn.softsys.blocoperatoire.domain.StatutIntervention;
import com.tn.softsys.blocoperatoire.domain.StatutSalle;
import com.tn.softsys.blocoperatoire.domain.TempsOperatoire;
import com.tn.softsys.blocoperatoire.domain.oms.ChecklistOms;
import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.ScoreType;
import com.tn.softsys.blocoperatoire.dto.dashboard.DashboardOverviewResponseDTO;
import com.tn.softsys.blocoperatoire.repository.ChecklistOmsRepository;
import com.tn.softsys.blocoperatoire.repository.IncidentSSPIRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.SSPIRepository;
import com.tn.softsys.blocoperatoire.repository.SalleRepository;
import com.tn.softsys.blocoperatoire.repository.ScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private static final int PRODUCTIVITY_TARGET = 90;
    private static final int QUALITY_WINDOW_DAYS = 30;

    private final InterventionRepository interventionRepository;
    private final SalleRepository salleRepository;
    private final SSPIRepository sspiRepository;
    private final IncidentSSPIRepository incidentSSPIRepository;
    private final ChecklistOmsRepository checklistOmsRepository;
    private final ScoreRepository scoreRepository;

    public DashboardOverviewResponseDTO getOverview(LocalDate referenceDate) {
        LocalDate day = referenceDate != null ? referenceDate : LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dayStart = day.atStartOfDay();
        LocalDateTime dayEndExclusive = day.plusDays(1).atStartOfDay();
        LocalDateTime dayEndInclusive = dayEndExclusive.minusNanos(1);
        LocalDateTime qualityStart = dayStart.minusDays(QUALITY_WINDOW_DAYS - 1L);

        List<Intervention> interventionsToday = interventionRepository.findByDateIntervention(day);
        List<Salle> salles = salleRepository.findAll();
        List<ChecklistOms> checklistsToday = checklistOmsRepository.findByIntervention_DateIntervention(day);
        List<SSPI> activeSspiEntries = sspiRepository.findByHeureSortieIsNull();
        List<SSPI> dischargesToday = sspiRepository.findByHeureSortieBetween(dayStart, dayEndInclusive);
        List<IncidentSSPI> qualityIncidents = incidentSSPIRepository
                .findByDeclaredAtBetweenOrderByDeclaredAtDesc(qualityStart, dayEndInclusive);

        Map<UUID, List<IncidentSSPI>> activeIncidentsBySspiId = groupIncidentsBySspiId(activeSspiEntries);
        Map<UUID, Integer> aldreteByInterventionId = loadLatestAldreteScores(activeSspiEntries);
        Map<UUID, ChecklistOms> checklistByInterventionId = checklistsToday.stream()
                .filter(item -> item.getIntervention() != null && item.getIntervention().getInterventionId() != null)
                .collect(Collectors.toMap(
                        item -> item.getIntervention().getInterventionId(),
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        List<Intervention> todayRelevant = interventionsToday.stream()
                .filter(item -> item.getStatut() != StatutIntervention.ANNULEE)
                .toList();
        List<Intervention> completedToday = todayRelevant.stream()
                .filter(this::isCompletedStatus)
                .toList();
        List<Intervention> cancelledToday = interventionsToday.stream()
                .filter(item -> item.getStatut() == StatutIntervention.ANNULEE)
                .toList();
        List<Intervention> inProgressToday = todayRelevant.stream()
                .filter(item -> item.getStatut() == StatutIntervention.EN_COURS)
                .toList();

        Set<UUID> occupiedRoomIds = inProgressToday.stream()
                .map(Intervention::getSalle)
                .filter(Objects::nonNull)
                .map(Salle::getSalleId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        int totalRooms = salles.size();
        int totalOperationalRooms = (int) salles.stream().filter(this::isOperationalRoom).count();
        int occupancyRate = computeRate(occupiedRoomIds.size(), totalOperationalRooms, 0);
        int averageTurnoverMinutes = computeAverageTurnoverMinutes(todayRelevant);

        DelayMetrics delayMetrics = computeDelayMetrics(todayRelevant, now);
        int checklistComplianceRate = computeRate(
                (int) todayRelevant.stream().filter(item -> isChecklistComplete(item, checklistByInterventionId)).count(),
                todayRelevant.size(),
                0
        );
        int onTimeRate = computeRate(
                todayRelevant.size() - delayMetrics.delayedCases(),
                todayRelevant.size(),
                100
        );
        int cancellationRate = computeRate(cancelledToday.size(), interventionsToday.size(), 0);
        int efficiencyRate = computeRate(completedToday.size(), todayRelevant.size(), 0);

        List<DashboardOverviewResponseDTO.ReanimationPatientSnapshot> reanimationPatients = activeSspiEntries.stream()
                .map(item -> toReanimationPatient(item, aldreteByInterventionId, activeIncidentsBySspiId))
                .sorted(Comparator.comparing(DashboardOverviewResponseDTO.ReanimationPatientSnapshot::patientLabel, String.CASE_INSENSITIVE_ORDER))
                .toList();

        int reinforcedMonitoring = (int) reanimationPatients.stream()
                .filter(item -> "reinforced".equals(item.state()))
                .count();
        int readyForDischarge = (int) reanimationPatients.stream()
                .filter(item -> "ready".equals(item.state()))
                .count();

        List<DashboardOverviewResponseDTO.QualityIncidentSnapshot> recentIncidents = qualityIncidents.stream()
                .map(this::toQualityIncident)
                .limit(5)
                .toList();

        int totalIncidents = qualityIncidents.size();
        int openIncidents = (int) qualityIncidents.stream().filter(item -> !Boolean.TRUE.equals(item.getResolu())).count();
        int criticalIncidents = (int) qualityIncidents.stream()
                .filter(item -> {
                    String severity = normalizeIncidentSeverity(item.getGravite());
                    return "critical".equals(severity) || "severe".equals(severity);
                })
                .count();
        int nearMisses = (int) qualityIncidents.stream()
                .filter(item -> "minor".equals(normalizeIncidentSeverity(item.getGravite())))
                .count();
        int resolutionRate = computeRate(
                (int) qualityIncidents.stream().filter(item -> Boolean.TRUE.equals(item.getResolu())).count(),
                totalIncidents,
                100
        );

        return new DashboardOverviewResponseDTO(
                day,
                LocalDateTime.now(),
                new DashboardOverviewResponseDTO.KpiSnapshot(
                        todayRelevant.size(),
                        completedToday.size(),
                        activeSspiEntries.size(),
                        reinforcedMonitoring,
                        occupiedRoomIds.size(),
                        totalRooms,
                        occupancyRate,
                        averageTurnoverMinutes
                ),
                new DashboardOverviewResponseDTO.PerformanceSnapshot(
                        checklistComplianceRate,
                        onTimeRate,
                        delayMetrics.averageDelayMinutes(),
                        cancellationRate
                ),
                new DashboardOverviewResponseDTO.DirectionSnapshot(
                        occupancyRate,
                        PRODUCTIVITY_TARGET,
                        todayRelevant.size(),
                        completedToday.size(),
                        todayRelevant.size(),
                        efficiencyRate,
                        checklistComplianceRate,
                        onTimeRate
                ),
                new DashboardOverviewResponseDTO.ReanimationSnapshot(
                        readyForDischarge,
                        dischargesToday.size(),
                        reanimationPatients
                ),
                new DashboardOverviewResponseDTO.QualitySnapshot(
                        totalIncidents,
                        openIncidents,
                        criticalIncidents,
                        nearMisses,
                        resolutionRate,
                        recentIncidents
                ),
                buildProcedureSnapshots(todayRelevant),
                buildRoomSnapshots(salles, todayRelevant)
        );
    }

    private Map<UUID, List<IncidentSSPI>> groupIncidentsBySspiId(List<SSPI> activeSspiEntries) {
        List<UUID> sspiIds = activeSspiEntries.stream()
                .map(SSPI::getSspiId)
                .filter(Objects::nonNull)
                .toList();

        if (sspiIds.isEmpty()) {
            return Map.of();
        }

        return incidentSSPIRepository.findBySspi_SspiIdIn(sspiIds).stream()
                .filter(item -> item.getSspi() != null && item.getSspi().getSspiId() != null)
                .collect(Collectors.groupingBy(item -> item.getSspi().getSspiId()));
    }

    private Map<UUID, Integer> loadLatestAldreteScores(List<SSPI> activeSspiEntries) {
        List<UUID> interventionIds = activeSspiEntries.stream()
                .map(SSPI::getIntervention)
                .filter(Objects::nonNull)
                .map(Intervention::getInterventionId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        if (interventionIds.isEmpty()) {
            return Map.of();
        }

        Map<UUID, Integer> values = new LinkedHashMap<>();
        for (Score score : scoreRepository.findByScoreTypeAndIntervention_InterventionIdInOrderByDateCalculDesc(
                ScoreType.ALDRETE,
                interventionIds
        )) {
            Intervention intervention = score.getIntervention();
            if (intervention == null || intervention.getInterventionId() == null) {
                continue;
            }
            values.putIfAbsent(intervention.getInterventionId(), score.getValeur());
        }

        return values;
    }

    private DelayMetrics computeDelayMetrics(List<Intervention> interventions, LocalDateTime now) {
        long totalDelay = 0L;
        int delayedCases = 0;

        for (Intervention intervention : interventions) {
            LocalDateTime scheduled = combine(intervention.getDateIntervention(), intervention.getHeureDebut());
            if (scheduled == null) {
                continue;
            }

            LocalDateTime actualStart = resolveActualStart(intervention);
            if (actualStart != null) {
                long delay = Math.max(0L, Duration.between(scheduled, actualStart).toMinutes());
                if (delay > 0L) {
                    totalDelay += delay;
                    delayedCases += 1;
                }
                continue;
            }

            if (intervention.getStatut() == StatutIntervention.PLANIFIEE && scheduled.isBefore(now)) {
                long delay = Math.max(0L, Duration.between(scheduled, now).toMinutes());
                if (delay > 0L) {
                    totalDelay += delay;
                    delayedCases += 1;
                }
            }
        }

        int averageDelay = delayedCases == 0 ? 0 : (int) Math.round((double) totalDelay / delayedCases);
        return new DelayMetrics(delayedCases, averageDelay);
    }

    private int computeAverageTurnoverMinutes(List<Intervention> interventions) {
        Map<UUID, List<Intervention>> byRoom = interventions.stream()
                .filter(item -> item.getSalle() != null && item.getSalle().getSalleId() != null)
                .collect(Collectors.groupingBy(item -> item.getSalle().getSalleId()));

        List<Long> gaps = new ArrayList<>();

        for (List<Intervention> roomItems : byRoom.values()) {
            List<Intervention> ordered = roomItems.stream()
                    .sorted(Comparator.comparing(this::resolveSequenceAnchor, Comparator.nullsLast(Comparator.naturalOrder())))
                    .toList();

            for (int index = 0; index < ordered.size() - 1; index++) {
                Intervention current = ordered.get(index);
                Intervention next = ordered.get(index + 1);

                LocalDateTime currentExit = resolveRoomExit(current);
                LocalDateTime nextEntry = resolveRoomEntry(next);
                if (currentExit == null || nextEntry == null) {
                    continue;
                }

                long gap = Duration.between(currentExit, nextEntry).toMinutes();
                if (gap >= 0L) {
                    gaps.add(gap);
                }
            }
        }

        if (gaps.isEmpty()) {
            return 0;
        }

        long total = gaps.stream().mapToLong(Long::longValue).sum();
        return (int) Math.round((double) total / gaps.size());
    }

    private List<DashboardOverviewResponseDTO.ProcedureSnapshot> buildProcedureSnapshots(List<Intervention> interventions) {
        return interventions.stream()
                .sorted(Comparator.comparing(item -> item.getHeureDebut() != null ? item.getHeureDebut() : LocalTime.MAX))
                .limit(6)
                .map(item -> new DashboardOverviewResponseDTO.ProcedureSnapshot(
                        item.getInterventionId(),
                        safe(item.getNomIntervention(), "Intervention"),
                        formatPatientLabel(item.getPatient()),
                        item.getStatut() != null ? item.getStatut().name() : StatutIntervention.PLANIFIEE.name(),
                        item.getHeureDebut() != null ? item.getHeureDebut().toString() : "",
                        item.getSalle() != null ? safe(item.getSalle().getNom(), "Salle non attribuee") : "Salle non attribuee"
                ))
                .toList();
    }

    private List<DashboardOverviewResponseDTO.RoomSnapshot> buildRoomSnapshots(
            List<Salle> salles,
            List<Intervention> interventionsToday
    ) {
        Map<UUID, List<Intervention>> byRoom = interventionsToday.stream()
                .filter(item -> item.getSalle() != null && item.getSalle().getSalleId() != null)
                .collect(Collectors.groupingBy(item -> item.getSalle().getSalleId()));

        return salles.stream()
                .sorted(Comparator.comparing(item -> safe(item.getNom(), ""), String.CASE_INSENSITIVE_ORDER))
                .map(room -> {
                    List<Intervention> roomItems = byRoom.getOrDefault(room.getSalleId(), List.of());
                    long scheduledCount = roomItems.stream()
                            .filter(item -> item.getStatut() == StatutIntervention.PLANIFIEE)
                            .count();
                    StatutSalle managedStatus = room.getStatut();
                    boolean operational = isOperationalRoom(room);

                    if (!operational && scheduledCount > 0L) {
                        return new DashboardOverviewResponseDTO.RoomSnapshot(
                                room.getSalleId(),
                                safe(room.getNom(), "Salle"),
                                "conflict",
                                scheduledCount + " intervention(s) planifiee(s) sur salle " + formatManagedStatusLabel(managedStatus).toLowerCase()
                        );
                    }

                    if (!operational) {
                        return new DashboardOverviewResponseDTO.RoomSnapshot(
                                room.getSalleId(),
                                safe(room.getNom(), "Salle"),
                                "inactive",
                                formatManagedStatusLabel(managedStatus)
                        );
                    }

                    Intervention inProgress = roomItems.stream()
                            .filter(item -> item.getStatut() == StatutIntervention.EN_COURS)
                            .findFirst()
                            .orElse(null);
                    if (inProgress != null || managedStatus == StatutSalle.EN_INTERVENTION) {
                        return new DashboardOverviewResponseDTO.RoomSnapshot(
                                room.getSalleId(),
                                safe(room.getNom(), "Salle"),
                                "occupied",
                                inProgress != null
                                        ? safe(inProgress.getNomIntervention(), "En intervention")
                                        : "Intervention en cours"
                        );
                    }

                    if (scheduledCount > 0L || managedStatus == StatutSalle.PLANIFIEE) {
                        return new DashboardOverviewResponseDTO.RoomSnapshot(
                                room.getSalleId(),
                                safe(room.getNom(), "Salle"),
                                "scheduled",
                                scheduledCount > 0L
                                        ? scheduledCount + " intervention(s) planifiee(s)"
                                        : "Salle reservee"
                        );
                    }

                    return new DashboardOverviewResponseDTO.RoomSnapshot(
                            room.getSalleId(),
                            safe(room.getNom(), "Salle"),
                            "available",
                            safe(room.getIdBloc(), "Disponible")
                    );
                })
                .toList();
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

    private String formatManagedStatusLabel(StatutSalle statutSalle) {
        if (statutSalle == null) {
            return "Fermee";
        }

        return switch (statutSalle) {
            case DISPONIBLE -> "Disponible";
            case PLANIFIEE -> "Planifiee";
            case EN_INTERVENTION -> "En intervention";
            case MAINTENANCE -> "Maintenance";
            case NETTOYAGE -> "Nettoyage";
            case FERMEE -> "Fermee";
        };
    }

    private DashboardOverviewResponseDTO.ReanimationPatientSnapshot toReanimationPatient(
            SSPI sspi,
            Map<UUID, Integer> aldreteByInterventionId,
            Map<UUID, List<IncidentSSPI>> incidentsBySspiId
    ) {
        Intervention intervention = sspi.getIntervention();
        Patient patient = intervention != null ? intervention.getPatient() : null;
        UUID interventionId = intervention != null ? intervention.getInterventionId() : null;
        Integer aldreteScore = interventionId != null ? aldreteByInterventionId.get(interventionId) : null;
        int openIncidentCount = (int) incidentsBySspiId.getOrDefault(sspi.getSspiId(), List.of()).stream()
                .filter(item -> !Boolean.TRUE.equals(item.getResolu()))
                .count();

        String state = "monitoring";
        if (openIncidentCount > 0 || (aldreteScore != null && aldreteScore < 9)) {
            state = "reinforced";
        } else if (aldreteScore != null && aldreteScore >= 9) {
            state = "ready";
        }

        return new DashboardOverviewResponseDTO.ReanimationPatientSnapshot(
                sspi.getSspiId(),
                patient != null ? patient.getPatientId() : null,
                interventionId,
                formatPatientLabel(patient),
                intervention != null ? safe(intervention.getNomIntervention(), "Intervention") : "Intervention",
                aldreteScore,
                openIncidentCount,
                state
        );
    }

    private DashboardOverviewResponseDTO.QualityIncidentSnapshot toQualityIncident(IncidentSSPI incident) {
        SSPI sspi = incident.getSspi();
        Intervention intervention = sspi != null ? sspi.getIntervention() : null;
        Patient patient = intervention != null ? intervention.getPatient() : null;

        return new DashboardOverviewResponseDTO.QualityIncidentSnapshot(
                incident.getIncidentId(),
                patient != null ? patient.getPatientId() : null,
                intervention != null ? intervention.getInterventionId() : null,
                formatPatientLabel(patient),
                intervention != null ? safe(intervention.getNomIntervention(), "Intervention") : "Intervention",
                prettify(incident.getType()),
                normalizeIncidentSeverity(incident.getGravite()),
                Boolean.TRUE.equals(incident.getResolu()),
                incident.getDeclaredAt()
        );
    }

    private boolean isChecklistComplete(Intervention intervention, Map<UUID, ChecklistOms> checklistByInterventionId) {
        ChecklistOms checklist = checklistByInterventionId.get(intervention.getInterventionId());
        return checklist != null
                && checklist.getSignIn() != null
                && checklist.getTimeOut() != null
                && checklist.getSignOut() != null;
    }

    private boolean isCompletedStatus(Intervention intervention) {
        return intervention.getStatut() == StatutIntervention.EN_SSPI
                || intervention.getStatut() == StatutIntervention.EN_REANIMATION
                || intervention.getStatut() == StatutIntervention.CLOTUREE;
    }

    private LocalDateTime resolveActualStart(Intervention intervention) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps == null) {
            return null;
        }

        if (temps.getEntreeBloc() != null) {
            return temps.getEntreeBloc();
        }
        if (temps.getDebutAnesthesie() != null) {
            return temps.getDebutAnesthesie();
        }
        return temps.getIncision();
    }

    private LocalDateTime resolveSequenceAnchor(Intervention intervention) {
        LocalDateTime roomEntry = resolveRoomEntry(intervention);
        return roomEntry != null ? roomEntry : combine(intervention.getDateIntervention(), intervention.getHeureDebut());
    }

    private LocalDateTime resolveRoomEntry(Intervention intervention) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null && temps.getEntreeBloc() != null) {
            return temps.getEntreeBloc();
        }
        return combine(intervention.getDateIntervention(), intervention.getHeureDebut());
    }

    private LocalDateTime resolveRoomExit(Intervention intervention) {
        TempsOperatoire temps = intervention.getTempsOperatoire();
        if (temps != null) {
            if (temps.getSortieSalle() != null) {
                return temps.getSortieSalle();
            }
            if (temps.getFinActe() != null) {
                return temps.getFinActe();
            }
        }

        LocalDateTime scheduled = combine(intervention.getDateIntervention(), intervention.getHeureDebut());
        Integer duration = intervention.getDureeReelle() != null ? intervention.getDureeReelle() : intervention.getDureePrevue();
        if (scheduled != null && duration != null && duration > 0) {
            return scheduled.plusMinutes(duration);
        }

        return null;
    }

    private LocalDateTime combine(LocalDate date, LocalTime time) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.of(date, time != null ? time : LocalTime.MIDNIGHT);
    }

    private int computeRate(int numerator, int denominator, int fallback) {
        if (denominator <= 0) {
            return fallback;
        }
        double value = (double) numerator * 100.0 / (double) denominator;
        return (int) Math.max(0, Math.min(100, Math.round(value)));
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

    private String normalizeIncidentSeverity(String value) {
        String normalized = safe(value, "").trim().toUpperCase();
        if (normalized.contains("CRIT")) {
            return "critical";
        }
        if (normalized.contains("SEVER") || normalized.contains("GRAVE")) {
            return "severe";
        }
        if (normalized.contains("MOD")) {
            return "moderate";
        }
        return "minor";
    }

    private String prettify(String value) {
        String normalized = safe(value, "Incident")
                .trim()
                .replace('_', ' ')
                .toLowerCase();

        if (normalized.isBlank()) {
            return "Incident";
        }

        String[] parts = normalized.split("\\s+");
        List<String> formatted = new ArrayList<>(parts.length);
        for (String part : parts) {
            if (part.isBlank()) {
                continue;
            }
            formatted.add(Character.toUpperCase(part.charAt(0)) + part.substring(1));
        }
        return String.join(" ", formatted);
    }

    private String safe(String value, String fallback) {
        return value != null ? value : fallback;
    }

    private record DelayMetrics(int delayedCases, int averageDelayMinutes) {}
}
