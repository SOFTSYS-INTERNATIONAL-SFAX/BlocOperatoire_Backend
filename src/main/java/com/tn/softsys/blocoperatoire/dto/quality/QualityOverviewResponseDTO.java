package com.tn.softsys.blocoperatoire.dto.quality;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record QualityOverviewResponseDTO(
        LocalDate referenceDate,
        LocalDateTime generatedAt,
        FilterSnapshot filters,
        TraceabilitySnapshot traceability,
        SummarySnapshot summary,
        ChecklistSnapshot checklist,
        PatientFlowSnapshot patientFlow,
        AllergySnapshot allergies,
        RoomDistributionSnapshot rooms,
        IncidentSnapshot incidents,
        List<QualityAlertSnapshot> alerts
) {

    public record FilterSnapshot(
            String selectedBloc,
            String selectedSpecialty,
            List<String> availableBlocs,
            List<String> availableSpecialties,
            List<LocalDate> availableDates
    ) {}

    public record TraceabilitySnapshot(
            int sourceInterventions,
            int sourceChecklists,
            int sourceIncidents,
            int operativeTimelineSources,
            int omsTimelineFallbackSources,
            int scheduledTimelineSources
    ) {}

    public record SummarySnapshot(
            int completedOmsChecklists,
            int checklistComplianceRate,
            int patientsWithAllergies,
            int averageEntryToIncisionMinutes,
            int openQualityAlerts
    ) {}

    public record ChecklistSnapshot(
            int interventionsAudited,
            int signInCompleted,
            int timeOutCompleted,
            int signOutCompleted,
            int fullyCompleted,
            int outstandingCount,
            int complianceRate
    ) {}

    public record PatientFlowSnapshot(
            int sampledInterventions,
            int totalEligibleInterventions,
            List<FlowMetricSnapshot> metrics
    ) {}

    public record FlowMetricSnapshot(
            String key,
            String label,
            int averageMinutes,
            int sampleCount,
            int operativeSourceCount,
            int omsFallbackSourceCount,
            int scheduledSourceCount,
            int missingSourceCount,
            int coverageRate,
            String reliability
    ) {}

    public record AllergySnapshot(
            int totalPatients,
            int allergicPatients,
            int nonAllergicPatients,
            int totalAllergyEntries,
            List<AllergySliceSnapshot> distribution
    ) {}

    public record AllergySliceSnapshot(
            String label,
            String normalizedKey,
            int count,
            int percentage
    ) {}

    public record RoomDistributionSnapshot(
            int totalRooms,
            int operationalRooms,
            int unavailableRooms,
            List<RoomTypeSnapshot> byType
    ) {}

    public record RoomTypeSnapshot(
            String typeLabel,
            String typeCode,
            int count,
            int operationalCount
    ) {}

    public record IncidentSnapshot(
            int totalIncidents,
            int openIncidents,
            int criticalIncidents,
            int nearMisses,
            int resolutionRate,
            List<IncidentItemSnapshot> recent
    ) {}

    public record IncidentItemSnapshot(
            UUID incidentId,
            String patientLabel,
            String interventionLabel,
            String typeLabel,
            String severity,
            boolean resolved,
            LocalDateTime declaredAt
    ) {}

    public record QualityAlertSnapshot(
            String category,
            String severity,
            String title,
            String detail,
            String referenceLabel,
            LocalDateTime occurredAt
    ) {}
}
