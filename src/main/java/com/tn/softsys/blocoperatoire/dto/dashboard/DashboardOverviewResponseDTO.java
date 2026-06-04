package com.tn.softsys.blocoperatoire.dto.dashboard;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record DashboardOverviewResponseDTO(
        LocalDate referenceDate,
        LocalDateTime generatedAt,
        KpiSnapshot kpis,
        PerformanceSnapshot performance,
        DirectionSnapshot direction,
        ReanimationSnapshot reanimation,
        QualitySnapshot quality,
        List<ProcedureSnapshot> procedures,
        List<RoomSnapshot> rooms
) {

    public record KpiSnapshot(
            int interventionsToday,
            int completedToday,
            int patientsInSspi,
            int reinforcedMonitoring,
            int occupiedRooms,
            int totalRooms,
            int occupancyRate,
            int averageTurnoverMinutes
    ) {}

    public record PerformanceSnapshot(
            int checklistComplianceRate,
            int onTimeRate,
            int averageDelayMinutes,
            int cancellationRate
    ) {}

    public record DirectionSnapshot(
            int productivityRate,
            int productivityTarget,
            int surgicalVolume,
            int completedToday,
            int plannedToday,
            int efficiencyRate,
            int qualityComplianceRate,
            int punctualityRate
    ) {}

    public record ReanimationSnapshot(
            int readyForDischarge,
            int dischargesToday,
            List<ReanimationPatientSnapshot> patients
    ) {}

    public record ReanimationPatientSnapshot(
            UUID sspiId,
            UUID patientId,
            UUID interventionId,
            String patientLabel,
            String interventionLabel,
            Integer aldreteScore,
            int openIncidentCount,
            String state
    ) {}

    public record QualitySnapshot(
            int totalIncidents,
            int openIncidents,
            int criticalIncidents,
            int nearMisses,
            int resolutionRate,
            List<QualityIncidentSnapshot> recentIncidents
    ) {}

    public record QualityIncidentSnapshot(
            UUID incidentId,
            UUID patientId,
            UUID interventionId,
            String patientLabel,
            String interventionLabel,
            String typeLabel,
            String severity,
            boolean resolved,
            LocalDateTime declaredAt
    ) {}

    public record ProcedureSnapshot(
            UUID interventionId,
            String procedure,
            String patientLabel,
            String status,
            String scheduledTime,
            String roomLabel
    ) {}

    public record RoomSnapshot(
            UUID salleId,
            String name,
            String state,
            String detail
    ) {}
}
