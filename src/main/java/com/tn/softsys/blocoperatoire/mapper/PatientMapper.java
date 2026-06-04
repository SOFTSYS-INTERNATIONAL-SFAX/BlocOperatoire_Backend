package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.PatientChangeLog;
import com.tn.softsys.blocoperatoire.dto.patient.PatientChangeLogDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientArchiveTraceDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Component
public class PatientMapper {

    /* ================= TO ENTITY ================= */

    public Patient toEntity(PatientRequestDTO dto) {

        return Patient.builder()
                .identiteFHIR(dto.getIdentiteFHIR())
                .mrn(dto.getMrn())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .nationalite(dto.getNationalite())
                .groupeSanguin(dto.getGroupeSanguin())
                .allergies(normalizeList(dto.getAllergies()))
                .traitementsHabituels(resolveStructuredList(dto.getTraitementsHabituels(), dto.getTraitementsEnCours()))
                .traitementsEnCours(resolveSummaryText(dto.getTraitementsEnCours(), dto.getTraitementsHabituels()))
                .antecedentsImportants(resolveStructuredList(dto.getAntecedentsImportants(), dto.getAntecedentsMedicaux()))
                .antecedentsMedicaux(resolveSummaryText(dto.getAntecedentsMedicaux(), dto.getAntecedentsImportants()))
                .tailleCm(dto.getTailleCm())
                .poidsKg(dto.getPoidsKg())
                .contactUrgenceNom(dto.getContactUrgenceNom())
                .contactUrgenceTelephone(dto.getContactUrgenceTelephone())
                .contactUrgenceRelation(dto.getContactUrgenceRelation())
                .contactUrgenceNotes(dto.getContactUrgenceNotes())
                .build();
    }
    public List<PatientChangeLogDTO> toChangeLogs(List<PatientChangeLog> logs) {
        return logs.stream().map(log ->
                PatientChangeLogDTO.builder()
                        .id(log.getId())
                        .fieldName(log.getFieldName())
                        .oldValue(log.getOldValue())
                        .newValue(log.getNewValue())
                        .changedAt(log.getChangedAt())
                        .changedByUserId(log.getChangedByUserId())
                        .changedBy(log.getChangedByDisplayName())
                        .build()
        ).toList();
    }
    /* ================= UPDATE ENTITY ================= */

    public void updateEntity(Patient entity, PatientRequestDTO dto) {

        entity.setIdentiteFHIR(dto.getIdentiteFHIR());
        entity.setMrn(dto.getMrn());
        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setDateNaissance(dto.getDateNaissance());
        entity.setSexe(dto.getSexe());
        entity.setNationalite(dto.getNationalite());
        entity.setGroupeSanguin(dto.getGroupeSanguin());
        entity.setAllergies(normalizeList(dto.getAllergies()));
        entity.setTraitementsHabituels(resolveStructuredList(dto.getTraitementsHabituels(), dto.getTraitementsEnCours()));
        entity.setTraitementsEnCours(resolveSummaryText(dto.getTraitementsEnCours(), dto.getTraitementsHabituels()));
        entity.setAntecedentsImportants(resolveStructuredList(dto.getAntecedentsImportants(), dto.getAntecedentsMedicaux()));
        entity.setAntecedentsMedicaux(resolveSummaryText(dto.getAntecedentsMedicaux(), dto.getAntecedentsImportants()));
        entity.setTailleCm(dto.getTailleCm());
        entity.setPoidsKg(dto.getPoidsKg());
        entity.setContactUrgenceNom(dto.getContactUrgenceNom());
        entity.setContactUrgenceTelephone(dto.getContactUrgenceTelephone());
        entity.setContactUrgenceRelation(dto.getContactUrgenceRelation());
        entity.setContactUrgenceNotes(dto.getContactUrgenceNotes());
    }

    /* ================= TO RESPONSE ================= */

    public PatientResponseDTO toResponse(Patient entity) {

        return PatientResponseDTO.builder()
                .patientId(entity.getPatientId())
                .identiteFHIR(entity.getIdentiteFHIR())
                .mrn(entity.getMrn())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .dateNaissance(entity.getDateNaissance())
                .sexe(entity.getSexe())
                .nationalite(entity.getNationalite())
                .groupeSanguin(entity.getGroupeSanguin())
                .allergies(normalizeList(entity.getAllergies()))
                .traitementsEnCours(entity.getTraitementsEnCours())
                .traitementsHabituels(resolveStructuredList(entity.getTraitementsHabituels(), entity.getTraitementsEnCours()))
                .antecedentsMedicaux(entity.getAntecedentsMedicaux())
                .antecedentsImportants(resolveStructuredList(entity.getAntecedentsImportants(), entity.getAntecedentsMedicaux()))
                .tailleCm(entity.getTailleCm())
                .poidsKg(entity.getPoidsKg())
                .imc(calculateImc(entity.getTailleCm(), entity.getPoidsKg()))
                .contactUrgenceNom(entity.getContactUrgenceNom())
                .contactUrgenceTelephone(entity.getContactUrgenceTelephone())
                .contactUrgenceRelation(entity.getContactUrgenceRelation())
                .contactUrgenceNotes(entity.getContactUrgenceNotes())
                .archived(entity.getArchived())
                .archiveTrace(entity.getArchivedAt() == null ? null : PatientArchiveTraceDTO.builder()
                        .patientId(entity.getPatientId())
                        .patientLabel(resolvePatientLabel(entity))
                        .archivedAt(entity.getArchivedAt())
                        .archivedByUserId(entity.getArchivedBy() != null ? entity.getArchivedBy().getUserId() : null)
                        .archivedBy(entity.getArchivedByDisplayName())
                        .reason(entity.getArchiveReason())
                        .build())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .updatedByDisplayName(entity.getUpdatedByDisplayName())
                .build();
    }

    /* ================= CALCUL IMC ================= */

    private Double calculateImc(Double tailleCm, Double poidsKg) {

        if (tailleCm == null || poidsKg == null) return null;

        double tailleMetre = tailleCm / 100.0;
        return poidsKg / (tailleMetre * tailleMetre);
    }

    private List<String> resolveStructuredList(List<String> values, String fallbackText) {
        List<String> normalized = normalizeList(values);

        if (!normalized.isEmpty()) {
            return normalized;
        }

        if (fallbackText == null || fallbackText.isBlank()) {
            return new ArrayList<>();
        }

        return normalizeList(List.of(fallbackText.split("[,;\\n]")));
    }

    private String resolveSummaryText(String summary, List<String> structuredValues) {
        if (summary != null && !summary.isBlank()) {
            return summary.trim();
        }

        List<String> normalized = normalizeList(structuredValues);
        return normalized.isEmpty() ? null : String.join(", ", normalized);
    }

    private List<String> normalizeList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return new ArrayList<>();
        }

        Set<String> normalized = new LinkedHashSet<>();

        for (String value : values) {
            if (value == null) {
                continue;
            }

            String trimmed = value.trim().replaceAll("\\s+", " ");

            if (!trimmed.isBlank()) {
                normalized.add(trimmed);
            }
        }

        return new ArrayList<>(normalized);
    }

    private String resolvePatientLabel(Patient entity) {
        String prenom = entity.getPrenom() == null ? "" : entity.getPrenom().trim();
        String nom = entity.getNom() == null ? "" : entity.getNom().trim();
        String label = (prenom + " " + nom).trim();
        return label.isBlank() ? entity.getMrn() : label;
    }
}
