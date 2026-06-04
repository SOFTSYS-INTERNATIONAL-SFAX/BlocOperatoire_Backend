package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.PatientClinicalHistoryEntry;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.patient.PatientClinicalHistoryRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientClinicalHistoryResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.PatientClinicalHistoryEntryRepository;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientClinicalHistoryService {

    private static final String MODULE = "PATIENT_HISTORY";

    private final PatientClinicalHistoryEntryRepository repository;
    private final PatientRepository patientRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    @Transactional(readOnly = true)
    public List<PatientClinicalHistoryResponseDTO> listByPatient(UUID patientId) {
        if (!patientRepository.existsById(patientId)) {
            throw new ResourceNotFoundException("Patient not found");
        }

        return repository.findByPatientPatientIdOrderByEventDateDescCreatedAtDesc(patientId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PatientClinicalHistoryResponseDTO create(UUID patientId, PatientClinicalHistoryRequestDTO dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        User currentUser = auditContextService.getCurrentUserOrNull();

        PatientClinicalHistoryEntry saved = repository.save(
                PatientClinicalHistoryEntry.builder()
                        .patient(patient)
                        .createdBy(currentUser)
                        .title(normalizeRequired(dto.getTitle(), "Clinical history title is required"))
                        .summary(normalizeRequired(dto.getSummary(), "Clinical history summary is required"))
                        .category(dto.getCategory())
                        .eventDate(dto.getEventDate())
                        .createdByDisplayName(resolveUserDisplayName(currentUser))
                        .build()
        );

        auditLogService.log(
                currentUser,
                "PATIENT_HISTORY_CREATE",
                MODULE,
                saved.getEntryId(),
                "Ajout historique patient=" + patientId
                        + " category=" + saved.getCategory()
                        + " title=" + saved.getTitle(),
                auditContextService.getClientIp()
        );

        return toResponse(saved);
    }

    public void delete(UUID entryId) {
        PatientClinicalHistoryEntry entry = repository.findById(entryId)
                .orElseThrow(() -> new ResourceNotFoundException("Clinical history entry not found"));

        auditLogService.log(
                auditContextService.getCurrentUserOrNull(),
                "PATIENT_HISTORY_DELETE",
                MODULE,
                entry.getEntryId(),
                "Suppression historique patient=" + entry.getPatient().getPatientId()
                        + " title=" + entry.getTitle(),
                auditContextService.getClientIp()
        );

        repository.delete(entry);
    }

    private PatientClinicalHistoryResponseDTO toResponse(PatientClinicalHistoryEntry entry) {
        return PatientClinicalHistoryResponseDTO.builder()
                .entryId(entry.getEntryId())
                .patientId(entry.getPatient().getPatientId())
                .title(entry.getTitle())
                .summary(entry.getSummary())
                .category(entry.getCategory())
                .eventDate(entry.getEventDate())
                .createdAt(entry.getCreatedAt())
                .createdByUserId(entry.getCreatedBy() != null ? entry.getCreatedBy().getUserId() : null)
                .createdBy(entry.getCreatedByDisplayName())
                .build();
    }

    private String normalizeRequired(String value, String message) {
        if (value == null) {
            throw new IllegalArgumentException(message);
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String resolveUserDisplayName(User user) {
        if (user == null) {
            return null;
        }

        String label = ((user.getPrenom() == null ? "" : user.getPrenom().trim()) + " "
                + (user.getNom() == null ? "" : user.getNom().trim())).trim();
        return label.isBlank() ? user.getEmail() : label;
    }
}
