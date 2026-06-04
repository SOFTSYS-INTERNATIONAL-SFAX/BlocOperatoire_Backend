package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDuplicateCandidateDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDuplicateRisk;
import com.tn.softsys.blocoperatoire.dto.patient.PatientMergeRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientMergeTraceResponseDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.mapper.PatientMapper;
import com.tn.softsys.blocoperatoire.repository.*;
import com.tn.softsys.blocoperatoire.specification.PatientSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PatientService {

    private static final String MODULE = "PATIENT";

    private final PatientRepository repository;
    private final PatientMapper mapper;
    private final AuditLogService auditLogService;
    private final PatientChangeLogRepository changeLogRepository;
    private final AuditContextService auditContextService;
    private final PatientDocumentRepository documentRepository;
    private final PatientClinicalHistoryEntryRepository clinicalHistoryRepository;
    private final PatientMergeTraceRepository mergeTraceRepository;

    public PatientResponseDTO create(PatientRequestDTO dto) {
        validateUniqueIdentity(dto.getIdentiteFHIR(), dto.getMrn(), null);

        Patient saved = repository.save(mapper.toEntity(dto));

        audit(
                "PATIENT_CREATE",
                saved.getPatientId(),
                "Creation patient mrn=" + saved.getMrn()
                        + " identiteFHIR=" + saved.getIdentiteFHIR()
                        + " nom=" + saved.getNom()
                        + " prenom=" + saved.getPrenom()
        );

        return buildResponse(saved, repository.findAllByArchivedFalse(), true);
    }
    private Patient clonePatient(Patient p) {
        return Patient.builder()
                .patientId(p.getPatientId())
                .identiteFHIR(p.getIdentiteFHIR())
                .mrn(p.getMrn())
                .nom(p.getNom())
                .prenom(p.getPrenom())
                .dateNaissance(p.getDateNaissance())
                .sexe(p.getSexe())
                .nationalite(p.getNationalite())
                .groupeSanguin(p.getGroupeSanguin())
                .allergies(p.getAllergies() == null ? new ArrayList<>() : new ArrayList<>(p.getAllergies()))
                .traitementsEnCours(p.getTraitementsEnCours())
                .traitementsHabituels(
                        p.getTraitementsHabituels() == null ? new ArrayList<>() : new ArrayList<>(p.getTraitementsHabituels())
                )
                .antecedentsMedicaux(p.getAntecedentsMedicaux())
                .antecedentsImportants(
                        p.getAntecedentsImportants() == null ? new ArrayList<>() : new ArrayList<>(p.getAntecedentsImportants())
                )
                .tailleCm(p.getTailleCm())
                .poidsKg(p.getPoidsKg())
                .contactUrgenceNom(p.getContactUrgenceNom())
                .contactUrgenceTelephone(p.getContactUrgenceTelephone())
                .contactUrgenceRelation(p.getContactUrgenceRelation())
                .contactUrgenceNotes(p.getContactUrgenceNotes())
                .build();
    }
    public PatientResponseDTO update(UUID id, PatientRequestDTO dto) {

        Patient existing = getRequiredPatient(id);

        validateUniqueIdentity(dto.getIdentiteFHIR(), dto.getMrn(), id);

        // 🔥 snapshot AVANT modification
        Patient old = clonePatient(existing);

        String oldMrn = existing.getMrn();
        String oldFhirIdentity = existing.getIdentiteFHIR();

        // 🔥 update entity
        mapper.updateEntity(existing, dto);

        // 🔥 audit user
        User user = auditContextService.getCurrentUserOrNull();
        existing.setUpdatedByDisplayName(resolveUserDisplayName(user));

        // 🔥 save
        Patient saved = repository.save(existing);

        // 🔥 CHANGE LOG (IMPORTANT)
        trackChanges(old, saved);

        // 🔥 audit log (tu gardes ton système)
        audit(
                "PATIENT_UPDATE",
                saved.getPatientId(),
                "Mise a jour patient mrnAvant=" + oldMrn
                        + " mrnApres=" + saved.getMrn()
                        + " identiteFHIRAvant=" + oldFhirIdentity
                        + " identiteFHIRApres=" + saved.getIdentiteFHIR()
        );

        // 🔥 retour normal (aucun changement ici)
        return buildResponse(saved, repository.findAllByArchivedFalse(), true);
    }

    @Transactional(readOnly = true)
    public PatientResponseDTO getById(UUID id) {

        Patient patient = getRequiredPatient(id);

        PatientResponseDTO response = mapper.toResponse(patient);

        response.setChangeLogs(
                mapper.toChangeLogs(
                        changeLogRepository.findByPatientIdOrderByChangedAtDesc(id)
                )
        );

        return response;
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> search(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toResponse);
    }

    @Transactional(readOnly = true)
    public long countBySexe(Sexe sexe) {
        return repository.countBySexe(sexe);
    }

    @Transactional(readOnly = true)
    public long countByGroupeSanguin(GroupeSanguin groupe) {
        return repository.countByGroupeSanguin(groupe);
    }

    @Transactional(readOnly = true)
    public List<PatientResponseDTO> getLast10Patients() {
        List<Patient> activePatients = repository.findAllByArchivedFalse();

        return repository.findTop10ByOrderByUpdatedAtDesc()
                .stream()
                .map(patient -> buildResponse(patient, activePatients, true))
                .toList();
    }

    public void delete(UUID id) {
        Patient patient = getRequiredPatient(id);

        if (!mergeTraceRepository
                .findByTargetPatientPatientIdOrSourcePatientPatientIdOrderByMergedAtDesc(id, id)
                .isEmpty()) {
            throw new IllegalStateException("Patient merged records cannot be deleted");
        }

        audit(
                "PATIENT_DELETE",
                patient.getPatientId(),
                "Suppression patient mrn=" + patient.getMrn()
                        + " nom=" + patient.getNom()
                        + " prenom=" + patient.getPrenom()
        );

        repository.delete(patient);
    }

    public PatientResponseDTO archive(UUID id, String reason) {
        Patient patient = getRequiredPatient(id);

        if (Boolean.TRUE.equals(patient.getArchived())) {
            throw new IllegalStateException("Patient already archived");
        }

        String normalizedReason = normalizeFreeText(reason);
        if (normalizedReason == null) {
            throw new IllegalArgumentException("Archive reason is required");
        }

        User currentUser = auditContextService.getCurrentUserOrNull();
        patient.setArchived(true);
        patient.setArchivedAt(LocalDateTime.now());
        patient.setArchivedBy(currentUser);
        patient.setArchivedByDisplayName(resolveUserDisplayName(currentUser));
        patient.setArchiveReason(normalizedReason);

        Patient saved = repository.save(patient);

        audit(
                "PATIENT_ARCHIVE",
                saved.getPatientId(),
                "Archivage patient mrn=" + saved.getMrn()
                        + " motif=" + normalizedReason
        );

        return buildResponse(saved, repository.findAll(), true);
    }

    @Transactional(readOnly = true)
    public Page<PatientResponseDTO> getArchived(Pageable pageable) {
        Page<Patient> patients = repository.findByArchivedTrue(pageable);
        List<Patient> allPatients = repository.findAll();
        return patients.map(patient -> buildResponse(patient, allPatients, true));
    }

    @Transactional(readOnly = true)
    public List<PatientDuplicateCandidateDTO> listDuplicates(UUID id) {
        Patient patient = getRequiredPatient(id);

        return repository.findAllByArchivedFalse()
                .stream()
                .filter(candidate -> !candidate.getPatientId().equals(id))
                .map(candidate -> mapDuplicateCandidate(patient, candidate))
                .filter(candidate -> candidate.getDuplicateRisk() != PatientDuplicateRisk.NONE)
                .sorted(
                        Comparator.comparingInt((PatientDuplicateCandidateDTO candidate) ->
                                        duplicateRank(candidate.getDuplicateRisk()))
                                .thenComparing(candidate -> safeLower(candidate.getNom()))
                                .thenComparing(candidate -> safeLower(candidate.getPrenom()))
                )
                .toList();
    }

    public PatientResponseDTO merge(PatientMergeRequestDTO dto) {
        if (dto.getTargetPatientId().equals(dto.getSourcePatientId())) {
            throw new IllegalArgumentException("Target patient and source patient must be different");
        }

        Patient target = getRequiredPatient(dto.getTargetPatientId());
        Patient source = getRequiredPatient(dto.getSourcePatientId());

        if (Boolean.TRUE.equals(target.getArchived())) {
            throw new IllegalStateException("Target patient must remain active");
        }

        String normalizedReason = normalizeFreeText(dto.getReason());
        if (normalizedReason == null) {
            throw new IllegalArgumentException("Merge reason is required");
        }

        User currentUser = auditContextService.getCurrentUserOrNull();
        String currentUserDisplayName = resolveUserDisplayName(currentUser);

        target.setAllergies(mergeLists(target.getAllergies(), source.getAllergies()));
        target.setTraitementsHabituels(mergeLists(target.getTraitementsHabituels(), source.getTraitementsHabituels()));
        target.setAntecedentsImportants(mergeLists(target.getAntecedentsImportants(), source.getAntecedentsImportants()));
        target.setTraitementsEnCours(mergeSummary(
                target.getTraitementsEnCours(),
                source.getTraitementsEnCours(),
                target.getTraitementsHabituels()
        ));
        target.setAntecedentsMedicaux(mergeSummary(
                target.getAntecedentsMedicaux(),
                source.getAntecedentsMedicaux(),
                target.getAntecedentsImportants()
        ));
        target.setNationalite(firstNonBlank(target.getNationalite(), source.getNationalite()));
        target.setContactUrgenceNom(firstNonBlank(target.getContactUrgenceNom(), source.getContactUrgenceNom()));
        target.setContactUrgenceTelephone(firstNonBlank(
                target.getContactUrgenceTelephone(),
                source.getContactUrgenceTelephone()
        ));
        target.setContactUrgenceRelation(firstNonBlank(
                target.getContactUrgenceRelation(),
                source.getContactUrgenceRelation()
        ));
        target.setContactUrgenceNotes(firstNonBlank(target.getContactUrgenceNotes(), source.getContactUrgenceNotes()));
        target.setTailleCm(target.getTailleCm() != null ? target.getTailleCm() : source.getTailleCm());
        target.setPoidsKg(target.getPoidsKg() != null ? target.getPoidsKg() : source.getPoidsKg());
        target.setGroupeSanguin(target.getGroupeSanguin() != null ? target.getGroupeSanguin() : source.getGroupeSanguin());

        Patient savedTarget = repository.save(target);

        List<PatientDocument> sourceDocuments = documentRepository.findByPatientPatientIdOrderByUploadedAtDesc(
                source.getPatientId()
        );
        sourceDocuments.forEach(document -> document.setPatient(savedTarget));
        documentRepository.saveAll(sourceDocuments);

        List<PatientClinicalHistoryEntry> sourceHistoryEntries =
                clinicalHistoryRepository.findByPatientPatientIdOrderByEventDateDescCreatedAtDesc(source.getPatientId());
        sourceHistoryEntries.forEach(entry -> entry.setPatient(savedTarget));
        clinicalHistoryRepository.saveAll(sourceHistoryEntries);

        source.setArchived(true);
        source.setArchivedAt(LocalDateTime.now());
        source.setArchivedBy(currentUser);
        source.setArchivedByDisplayName(currentUserDisplayName);
        source.setArchiveReason("Fusion dans dossier " + savedTarget.getMrn() + " - " + normalizedReason);
        repository.save(source);

        PatientMergeTrace trace = mergeTraceRepository.save(
                PatientMergeTrace.builder()
                        .targetPatient(savedTarget)
                        .sourcePatient(source)
                        .mergedBy(currentUser)
                        .mergedByDisplayName(currentUserDisplayName)
                        .reason(normalizedReason)
                        .build()
        );

        audit(
                "PATIENT_MERGE",
                savedTarget.getPatientId(),
                "Fusion patient cibleMRN=" + savedTarget.getMrn()
                        + " sourceMRN=" + source.getMrn()
                        + " mergeId=" + trace.getMergeTraceId()
                        + " motif=" + normalizedReason
        );

        return buildResponse(savedTarget, repository.findAllByArchivedFalse(), true);
    }

    private PatientResponseDTO buildResponse(Patient patient, List<Patient> comparisonPatients, boolean includeMergeHistory) {
        PatientResponseDTO response = mapper.toResponse(patient);

        response.setDocumentCount(documentRepository.countByPatientPatientId(patient.getPatientId()));
        response.setClinicalHistoryCount(clinicalHistoryRepository.countByPatientPatientId(patient.getPatientId()));

        DuplicateAssessment duplicateAssessment = assessDuplicate(patient, comparisonPatients);
        response.setDuplicateRisk(duplicateAssessment.risk());
        response.setDuplicateReasons(duplicateAssessment.reasons());
        response.setDuplicatePatientIds(duplicateAssessment.candidateIds());

        if (includeMergeHistory) {
            response.setMergeHistory(buildMergeHistory(patient.getPatientId()));
        }

        return response;
    }

    private List<PatientMergeTraceResponseDTO> buildMergeHistory(UUID patientId) {
        return mergeTraceRepository.findByTargetPatientPatientIdOrSourcePatientPatientIdOrderByMergedAtDesc(patientId, patientId)
                .stream()
                .map(trace -> PatientMergeTraceResponseDTO.builder()
                        .id(trace.getMergeTraceId())
                        .targetPatientId(trace.getTargetPatient().getPatientId())
                        .targetPatientLabel(resolvePatientLabel(trace.getTargetPatient()))
                        .sourcePatientId(trace.getSourcePatient().getPatientId())
                        .sourcePatientLabel(resolvePatientLabel(trace.getSourcePatient()))
                        .mergedAt(trace.getMergedAt())
                        .mergedByUserId(trace.getMergedBy() != null ? trace.getMergedBy().getUserId() : null)
                        .mergedBy(trace.getMergedByDisplayName())
                        .reason(trace.getReason())
                        .direction(patientId.equals(trace.getTargetPatient().getPatientId()) ? "TARGET" : "SOURCE")
                        .build())
                .toList();
    }

    private PatientDuplicateCandidateDTO mapDuplicateCandidate(Patient reference, Patient candidate) {
        DuplicateCandidateAssessment assessment = assessCandidate(reference, candidate);

        return PatientDuplicateCandidateDTO.builder()
                .patientId(candidate.getPatientId())
                .mrn(candidate.getMrn())
                .identiteFHIR(candidate.getIdentiteFHIR())
                .nom(candidate.getNom())
                .prenom(candidate.getPrenom())
                .dateNaissance(candidate.getDateNaissance())
                .contactUrgenceNom(candidate.getContactUrgenceNom())
                .contactUrgenceTelephone(candidate.getContactUrgenceTelephone())
                .duplicateRisk(assessment.risk())
                .duplicateReasons(assessment.reasons())
                .build();
    }

    private DuplicateAssessment assessDuplicate(Patient patient, List<Patient> comparisonPatients) {
        if (comparisonPatients == null || comparisonPatients.isEmpty()) {
            return DuplicateAssessment.none();
        }

        PatientDuplicateRisk highestRisk = PatientDuplicateRisk.NONE;
        Set<String> reasons = new LinkedHashSet<>();
        Set<UUID> candidateIds = new LinkedHashSet<>();

        for (Patient candidate : comparisonPatients) {
            if (candidate == null || Objects.equals(candidate.getPatientId(), patient.getPatientId())) {
                continue;
            }

            DuplicateCandidateAssessment assessment = assessCandidate(patient, candidate);
            if (assessment.risk() == PatientDuplicateRisk.NONE) {
                continue;
            }

            candidateIds.add(candidate.getPatientId());
            highestRisk = maxRisk(highestRisk, assessment.risk());
            reasons.addAll(assessment.reasons());
        }

        return new DuplicateAssessment(highestRisk, new ArrayList<>(reasons), new ArrayList<>(candidateIds));
    }

    private DuplicateCandidateAssessment assessCandidate(Patient patient, Patient candidate) {
        PatientDuplicateRisk risk = PatientDuplicateRisk.NONE;
        List<String> reasons = new ArrayList<>();
        String candidateLabel = resolvePatientLabel(candidate) + " (" + candidate.getMrn() + ")";

        if (sameText(patient.getIdentiteFHIR(), candidate.getIdentiteFHIR())) {
            risk = PatientDuplicateRisk.HIGH;
            reasons.add("Identite FHIR identique avec " + candidateLabel);
        }

        if (sameText(patient.getMrn(), candidate.getMrn())) {
            risk = PatientDuplicateRisk.HIGH;
            reasons.add("MRN identique avec " + candidateLabel);
        }

        if (sameText(patient.getNom(), candidate.getNom())
                && sameText(patient.getPrenom(), candidate.getPrenom())
                && Objects.equals(patient.getDateNaissance(), candidate.getDateNaissance())) {
            risk = PatientDuplicateRisk.HIGH;
            reasons.add("Nom, prenom et date de naissance identiques avec " + candidateLabel);
        } else if (sameText(patient.getNom(), candidate.getNom())
                && sameText(patient.getPrenom(), candidate.getPrenom())
                && sameText(patient.getContactUrgenceTelephone(), candidate.getContactUrgenceTelephone())) {
            risk = maxRisk(risk, PatientDuplicateRisk.REVIEW);
            reasons.add("Nom, prenom et telephone d'urgence proches avec " + candidateLabel);
        } else if (sameText(patient.getNom(), candidate.getNom())
                && Objects.equals(patient.getDateNaissance(), candidate.getDateNaissance())) {
            risk = maxRisk(risk, PatientDuplicateRisk.REVIEW);
            reasons.add("Nom et date de naissance similaires avec " + candidateLabel);
        }

        return new DuplicateCandidateAssessment(risk, reasons);
    }

    private PatientDuplicateRisk maxRisk(PatientDuplicateRisk left, PatientDuplicateRisk right) {
        return duplicateRank(left) <= duplicateRank(right) ? left : right;
    }

    private int duplicateRank(PatientDuplicateRisk risk) {
        if (risk == null) {
            return 99;
        }

        return switch (risk) {
            case HIGH -> 0;
            case REVIEW -> 1;
            case NONE -> 2;
        };
    }

    private List<String> mergeLists(List<String> currentValues, List<String> incomingValues) {
        LinkedHashSet<String> merged = new LinkedHashSet<>();

        for (String value : currentValues == null ? List.<String>of() : currentValues) {
            String normalized = normalizeFreeText(value);
            if (normalized != null) {
                merged.add(normalized);
            }
        }

        for (String value : incomingValues == null ? List.<String>of() : incomingValues) {
            String normalized = normalizeFreeText(value);
            if (normalized != null) {
                merged.add(normalized);
            }
        }

        return new ArrayList<>(merged);
    }

    private String mergeSummary(String currentSummary, String incomingSummary, List<String> structuredValues) {
        if (structuredValues != null && !structuredValues.isEmpty()) {
            return String.join(", ", mergeLists(structuredValues, List.of()));
        }

        return firstNonBlank(currentSummary, incomingSummary);
    }

    private void validateUniqueIdentity(String identiteFHIR, String mrn, UUID currentPatientId) {
        repository.findByIdentiteFHIR(identiteFHIR)
                .filter(patient -> !patient.getPatientId().equals(currentPatientId))
                .ifPresent(patient -> {
                    throw new IllegalStateException("FHIR identity already exists");
                });

        repository.findByMrn(mrn)
                .filter(patient -> !patient.getPatientId().equals(currentPatientId))
                .ifPresent(patient -> {
                    throw new IllegalStateException("MRN already exists");
                });
    }

    private Patient getRequiredPatient(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
    }

    private boolean sameText(String left, String right) {
        String normalizedLeft = normalizeComparableText(left);
        String normalizedRight = normalizeComparableText(right);
        return normalizedLeft != null && normalizedLeft.equals(normalizedRight);
    }

    private String normalizeComparableText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized.toLowerCase();
    }

    private String firstNonBlank(String primary, String secondary) {
        String normalizedPrimary = normalizeFreeText(primary);
        if (normalizedPrimary != null) {
            return normalizedPrimary;
        }
        return normalizeFreeText(secondary);
    }

    private String normalizeFreeText(String value) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        return normalized.isBlank() ? null : normalized;
    }

    private String resolvePatientLabel(Patient patient) {
        String label = (safeValue(patient.getPrenom()) + " " + safeValue(patient.getNom())).trim();
        return label.isBlank() ? patient.getMrn() : label;
    }

    private String resolveUserDisplayName(User user) {
        if (user == null) {
            return null;
        }

        String label = (safeValue(user.getPrenom()) + " " + safeValue(user.getNom())).trim();
        return label.isBlank() ? user.getEmail() : label;
    }

    private String safeValue(String value) {
        return value == null ? "" : value.trim();
    }

    private String safeLower(String value) {
        return value == null ? "" : value.toLowerCase();
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

    private record DuplicateCandidateAssessment(PatientDuplicateRisk risk, List<String> reasons) {
    }

    private record DuplicateAssessment(PatientDuplicateRisk risk, List<String> reasons, List<UUID> candidateIds) {

        private static DuplicateAssessment none() {
            return new DuplicateAssessment(PatientDuplicateRisk.NONE, List.of(), List.of());
        }
    }
    private void compare(List<PatientChangeLog> logs,
                         String field,
                         Object oldVal,
                         Object newVal,
                         UUID patientId) {

        if (!Objects.equals(oldVal, newVal)) {

            User user = auditContextService.getCurrentUserOrNull();

            logs.add(PatientChangeLog.builder()
                    .patientId(patientId)
                    .fieldName(field)
                    .oldValue(formatChangeValue(oldVal))
                    .newValue(formatChangeValue(newVal))
                    .changedByUserId(user != null ? user.getUserId() : null)
                    .changedByDisplayName(resolveUserDisplayName(user))
                    .build());
        }
    }
    private void trackChanges(Patient oldPatient, Patient newPatient) {

        List<PatientChangeLog> logs = new ArrayList<>();

        compare(logs, "identiteFHIR", oldPatient.getIdentiteFHIR(), newPatient.getIdentiteFHIR(), oldPatient.getPatientId());
        compare(logs, "mrn", oldPatient.getMrn(), newPatient.getMrn(), oldPatient.getPatientId());
        compare(logs, "nom", oldPatient.getNom(), newPatient.getNom(), oldPatient.getPatientId());
        compare(logs, "prenom", oldPatient.getPrenom(), newPatient.getPrenom(), oldPatient.getPatientId());
        compare(logs, "dateNaissance", oldPatient.getDateNaissance(), newPatient.getDateNaissance(), oldPatient.getPatientId());
        compare(logs, "sexe", oldPatient.getSexe(), newPatient.getSexe(), oldPatient.getPatientId());
        compare(logs, "nationalite", oldPatient.getNationalite(), newPatient.getNationalite(), oldPatient.getPatientId());
        compare(logs, "groupeSanguin", oldPatient.getGroupeSanguin(), newPatient.getGroupeSanguin(), oldPatient.getPatientId());
        compare(logs, "tailleCm", oldPatient.getTailleCm(), newPatient.getTailleCm(), oldPatient.getPatientId());
        compare(logs, "poidsKg", oldPatient.getPoidsKg(), newPatient.getPoidsKg(), oldPatient.getPatientId());
        compare(logs, "allergies", oldPatient.getAllergies(), newPatient.getAllergies(), oldPatient.getPatientId());
        compare(logs, "traitementsEnCours", oldPatient.getTraitementsEnCours(), newPatient.getTraitementsEnCours(), oldPatient.getPatientId());
        compare(logs, "traitementsHabituels", oldPatient.getTraitementsHabituels(), newPatient.getTraitementsHabituels(), oldPatient.getPatientId());
        compare(logs, "antecedentsMedicaux", oldPatient.getAntecedentsMedicaux(), newPatient.getAntecedentsMedicaux(), oldPatient.getPatientId());
        compare(logs, "antecedentsImportants", oldPatient.getAntecedentsImportants(), newPatient.getAntecedentsImportants(), oldPatient.getPatientId());
        compare(logs, "contactUrgenceNom", oldPatient.getContactUrgenceNom(), newPatient.getContactUrgenceNom(), oldPatient.getPatientId());
        compare(logs, "contactUrgenceTelephone", oldPatient.getContactUrgenceTelephone(), newPatient.getContactUrgenceTelephone(), oldPatient.getPatientId());
        compare(logs, "contactUrgenceRelation", oldPatient.getContactUrgenceRelation(), newPatient.getContactUrgenceRelation(), oldPatient.getPatientId());
        compare(logs, "contactUrgenceNotes", oldPatient.getContactUrgenceNotes(), newPatient.getContactUrgenceNotes(), oldPatient.getPatientId());

        if (!logs.isEmpty()) {
            changeLogRepository.saveAll(logs);
        }
    }

    private String formatChangeValue(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof List<?> list) {
            List<String> normalized = list.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .map(String::trim)
                    .filter(item -> !item.isBlank())
                    .toList();

            return normalized.isEmpty() ? null : String.join(", ", normalized);
        }

        String normalized = String.valueOf(value).trim();
        return normalized.isBlank() ? null : normalized;
    }
}
