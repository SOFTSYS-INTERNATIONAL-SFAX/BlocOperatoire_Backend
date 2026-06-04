package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import com.tn.softsys.blocoperatoire.domain.ConsultationPreAnesthesique;
import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.ConsultationPreAnesthesiqueRequestDTO;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.ConsultationPreAnesthesiqueResponseDTO;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ConsentementRepository;
import com.tn.softsys.blocoperatoire.repository.ConsultationPreAnesthesiqueRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import com.tn.softsys.blocoperatoire.repository.PatientRepository;
import com.tn.softsys.blocoperatoire.repository.PreAnesthesieDocumentRepository;
import com.tn.softsys.blocoperatoire.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ConsultationPreAnesthesiqueService {

    private static final String MODULE = "PRE_ANESTHESIE";

    private final ConsultationPreAnesthesiqueRepository repository;
    private final PatientRepository patientRepository;
    private final ConsentementRepository consentementRepository;
    private final InterventionRepository interventionRepository;
    private final PreAnesthesieDocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;

    public ConsultationPreAnesthesiqueResponseDTO create(ConsultationPreAnesthesiqueRequestDTO dto) {
        ConsultationPreAnesthesique entity = new ConsultationPreAnesthesique();
        apply(dto, entity);

        ConsultationPreAnesthesique saved = repository.save(entity);

        audit(
                "PRE_ANESTHESIE_CREATE",
                saved.getConsultationId(),
                "Creation consultation pre-anesthesique patient=" + saved.getPatient().getPatientId()
                        + " intervention=" + saved.getIntervention().getInterventionId()
                        + " anesthesiste=" + textOrDash(resolveUserDisplayName(saved.getAnesthesiste()))
                        + " asaCode=" + saved.getAsaCode()
                        + " typeAnesthesie=" + saved.getTypeAnesthesie()
                        + " validee=" + saved.getValidee()
        );

        if (Boolean.TRUE.equals(saved.getValidee())) {
            audit(
                    "PRE_ANESTHESIE_VALIDATE",
                    saved.getConsultationId(),
                    "Validation consultation pre-anesthesique patient=" + saved.getPatient().getPatientId()
            );
        }

        return toResponse(saved);
    }

    public ConsultationPreAnesthesiqueResponseDTO update(UUID id, ConsultationPreAnesthesiqueRequestDTO dto) {
        ConsultationPreAnesthesique entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation pre-anesthesique not found"));

        if (Boolean.TRUE.equals(entity.getValidee())) {
            throw new IllegalStateException("Validated pre-anesthesia consultation is locked and cannot be modified");
        }

        Boolean wasValidated = entity.getValidee();

        apply(dto, entity);

        ConsultationPreAnesthesique saved = repository.save(entity);

        audit(
                "PRE_ANESTHESIE_UPDATE",
                saved.getConsultationId(),
                "Mise a jour consultation pre-anesthesique patient=" + saved.getPatient().getPatientId()
                        + " intervention=" + saved.getIntervention().getInterventionId()
                        + " anesthesiste=" + textOrDash(resolveUserDisplayName(saved.getAnesthesiste()))
                        + " asaCode=" + saved.getAsaCode()
                        + " typeAnesthesie=" + saved.getTypeAnesthesie()
                        + " validee=" + saved.getValidee()
        );

        if (!Boolean.TRUE.equals(wasValidated) && Boolean.TRUE.equals(saved.getValidee())) {
            audit(
                    "PRE_ANESTHESIE_VALIDATE",
                    saved.getConsultationId(),
                    "Validation consultation pre-anesthesique patient=" + saved.getPatient().getPatientId()
            );
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ConsultationPreAnesthesiqueResponseDTO getById(UUID id) {
        return repository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation pre-anesthesique not found"));
    }

    @Transactional(readOnly = true)
    public Page<ConsultationPreAnesthesiqueResponseDTO> search(UUID patientId, Pageable pageable) {
        Pageable effectivePageable = pageable.getSort().isSorted()
                ? pageable
                : PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Order.desc("updatedAt"), Sort.Order.desc("createdAt"))
        );

        Page<ConsultationPreAnesthesique> page = patientId == null
                ? repository.findAll(effectivePageable)
                : repository.findByPatient_PatientId(patientId, effectivePageable);

        return page.map(this::toResponse);
    }

    private void apply(ConsultationPreAnesthesiqueRequestDTO dto, ConsultationPreAnesthesique entity) {
        Patient patient = patientRepository.findById(dto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        Consentement consentement = null;
        if (dto.getConsentementId() != null) {
            consentement = consentementRepository.findById(dto.getConsentementId())
                    .orElseThrow(() -> new ResourceNotFoundException("Consentement not found"));
        }

        Intervention intervention = resolveIntervention(dto.getInterventionId(), consentement);
        User anesthesiste = resolveAnesthesiste(dto.getAnesthesisteId(), intervention);
        validateLinks(patient, consentement, intervention);

        User currentUser = auditContextService.getCurrentUserOrNull();
        String currentUserLabel = resolveUserDisplayName(currentUser);

        entity.setPatient(patient);
        entity.setConsentement(consentement);
        entity.setIntervention(intervention);
        entity.setAnesthesiste(anesthesiste);
        entity.setAsaId(dto.getAsaId());
        entity.setAsaCode(normalizeAsaCode(dto.getAsaCode()));
        entity.setUrgence(Boolean.TRUE.equals(dto.getUrgence()));
        entity.setPoidsKg(dto.getPoidsKg());
        entity.setTailleCm(dto.getTailleCm());
        entity.setPaSystolique(dto.getPaSystolique());
        entity.setPaDiastolique(dto.getPaDiastolique());
        entity.setFrequenceCardiaque(dto.getFrequenceCardiaque());
        entity.setFrequenceRespiratoire(dto.getFrequenceRespiratoire());
        entity.setSpo2(dto.getSpo2());
        entity.setTemperatureDixieme(dto.getTemperatureDixieme());
        entity.setMallampatiCode(normalizeText(dto.getMallampatiCode(), 30));
        entity.setOuvertureBucaleMm(dto.getOuvertureBucaleMm());
        entity.setMobiliteCervicale(normalizeText(dto.getMobiliteCervicale(), 30));
        entity.setTypeAnesthesie(normalizeText(dto.getTypeAnesthesie(), 60));
        entity.setConsiderations(normalizeText(dto.getConsiderations(), 2000));
        entity.setAllergiesResume(normalizeText(dto.getAllergiesResume(), 2000));
        entity.setTraitementsChroniques(normalizeText(dto.getTraitementsChroniques(), 2000));
        entity.setAntecedentsMedicauxResume(normalizeText(dto.getAntecedentsMedicauxResume(), 2000));
        entity.setAntecedentsAnesthesiques(normalizeText(dto.getAntecedentsAnesthesiques(), 2000));
        entity.setEvaluationCardioRespiratoire(normalizeText(dto.getEvaluationCardioRespiratoire(), 2000));
        entity.setExamensComplementairesResume(normalizeText(dto.getExamensComplementairesResume(), 2000));
        entity.setEtatDentaire(normalizeText(dto.getEtatDentaire(), 500));
        entity.setRisqueHemorragique(normalizeText(dto.getRisqueHemorragique(), 500));
        entity.setStrategieAnesthesique(normalizeText(dto.getStrategieAnesthesique(), 2000));
        entity.setJeuneConfirme(Boolean.TRUE.equals(dto.getJeuneConfirme()));
        entity.setJeuneHeures(dto.getJeuneHeures());
        entity.setConsentementEclaireObtenu(Boolean.TRUE.equals(dto.getConsentementEclaireObtenu()));
        entity.setVoieAerienneDifficileSuspectee(Boolean.TRUE.equals(dto.getVoieAerienneDifficileSuspectee()));
        entity.setNotesComplementaires(normalizeText(dto.getNotesComplementaires(), 2000));

        if (currentUserLabel != null) {
            entity.setMedecinNom(currentUserLabel);
        }

        validateClinicalRanges(entity);

        boolean validated = Boolean.TRUE.equals(dto.getValidee());
        entity.setValidee(validated);

        if (validated) {
            validateFormalApproval(entity, consentement, intervention, currentUser);
            entity.setValidatedAt(LocalDateTime.now());
            entity.setValidatedBy(currentUser);
            entity.setValidatedByName(currentUserLabel);
            entity.setValidationCommentaire(normalizeText(dto.getValidationCommentaire(), 2000));
        } else {
            entity.setValidatedAt(null);
            entity.setValidatedBy(null);
            entity.setValidatedByName(null);
            entity.setValidationCommentaire(normalizeText(dto.getValidationCommentaire(), 2000));
        }

        applyRiskScore(entity);
    }

    private ConsultationPreAnesthesiqueResponseDTO toResponse(ConsultationPreAnesthesique entity) {
        return ConsultationPreAnesthesiqueResponseDTO.builder()
                .consultationId(entity.getConsultationId())
                .patientId(entity.getPatient().getPatientId())
                .consentementId(entity.getConsentement() != null ? entity.getConsentement().getConsentId() : null)
                .interventionId(entity.getIntervention() != null ? entity.getIntervention().getInterventionId() : null)
                .anesthesisteId(entity.getAnesthesiste() != null ? entity.getAnesthesiste().getUserId() : null)
                .anesthesisteNom(resolveUserDisplayName(entity.getAnesthesiste()))
                .interventionNom(entity.getIntervention() != null ? entity.getIntervention().getNomIntervention() : null)
                .interventionDate(entity.getIntervention() != null ? entity.getIntervention().getDateIntervention() : null)
                .interventionHeure(entity.getIntervention() != null ? entity.getIntervention().getHeureDebut() : null)
                .asaId(entity.getAsaId())
                .asaCode(entity.getAsaCode())
                .urgence(entity.getUrgence())
                .poidsKg(entity.getPoidsKg())
                .tailleCm(entity.getTailleCm())
                .paSystolique(entity.getPaSystolique())
                .paDiastolique(entity.getPaDiastolique())
                .frequenceCardiaque(entity.getFrequenceCardiaque())
                .frequenceRespiratoire(entity.getFrequenceRespiratoire())
                .spo2(entity.getSpo2())
                .temperatureDixieme(entity.getTemperatureDixieme())
                .mallampatiCode(entity.getMallampatiCode())
                .ouvertureBucaleMm(entity.getOuvertureBucaleMm())
                .mobiliteCervicale(entity.getMobiliteCervicale())
                .typeAnesthesie(entity.getTypeAnesthesie())
                .considerations(entity.getConsiderations())
                .allergiesResume(entity.getAllergiesResume())
                .traitementsChroniques(entity.getTraitementsChroniques())
                .antecedentsMedicauxResume(entity.getAntecedentsMedicauxResume())
                .antecedentsAnesthesiques(entity.getAntecedentsAnesthesiques())
                .evaluationCardioRespiratoire(entity.getEvaluationCardioRespiratoire())
                .examensComplementairesResume(entity.getExamensComplementairesResume())
                .etatDentaire(entity.getEtatDentaire())
                .risqueHemorragique(entity.getRisqueHemorragique())
                .strategieAnesthesique(entity.getStrategieAnesthesique())
                .jeuneConfirme(entity.getJeuneConfirme())
                .jeuneHeures(entity.getJeuneHeures())
                .consentementEclaireObtenu(entity.getConsentementEclaireObtenu())
                .voieAerienneDifficileSuspectee(entity.getVoieAerienneDifficileSuspectee())
                .notesComplementaires(entity.getNotesComplementaires())
                .validee(entity.getValidee())
                .medecinNom(entity.getMedecinNom())
                .validatedByUserId(entity.getValidatedBy() != null ? entity.getValidatedBy().getUserId() : null)
                .validatedByName(entity.getValidatedByName())
                .validatedAt(entity.getValidatedAt())
                .validationCommentaire(entity.getValidationCommentaire())
                .riskScore(entity.getRiskScore())
                .riskLevel(entity.getRiskLevel())
                .riskSummary(entity.getRiskSummary())
                .documentCount(resolveDocumentCount(entity.getConsultationId()))
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private long resolveDocumentCount(UUID consultationId) {
        if (consultationId == null) {
            return 0L;
        }

        try {
            return documentRepository.countByConsultationConsultationId(consultationId);
        } catch (RuntimeException ex) {
            log.warn("Unable to count pre-anesthesia documents for consultation {}", consultationId, ex);
            return 0L;
        }
    }

    private Intervention resolveIntervention(UUID interventionId, Consentement consentement) {
        if (interventionId != null) {
            return interventionRepository.findById(interventionId)
                    .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));
        }

        if (consentement != null && consentement.getIntervention() != null) {
            return consentement.getIntervention();
        }

        throw new IllegalArgumentException("Linked planned intervention is required");
    }

    private User resolveAnesthesiste(UUID anesthesisteId, Intervention intervention) {
        if (anesthesisteId != null) {
            return userRepository.findById(anesthesisteId)
                    .orElseThrow(() -> new ResourceNotFoundException("Anesthesiste not found"));
        }

        return intervention != null ? intervention.getAnesthesiste() : null;
    }

    private void validateLinks(Patient patient, Consentement consentement, Intervention intervention) {
        if (intervention == null) {
            throw new IllegalArgumentException("Linked planned intervention is required");
        }

        if (!intervention.getPatient().getPatientId().equals(patient.getPatientId())) {
            throw new IllegalArgumentException("The planned intervention does not match the selected patient");
        }

        if (consentement == null) {
            return;
        }

        if (!consentement.getPatient().getPatientId().equals(patient.getPatientId())) {
            throw new IllegalArgumentException("The consent does not match the selected patient");
        }

        if (consentement.getIntervention() == null
                || !consentement.getIntervention().getInterventionId().equals(intervention.getInterventionId())) {
            throw new IllegalArgumentException("The consent does not match the planned intervention");
        }
    }

    private void validateFormalApproval(
            ConsultationPreAnesthesique entity,
            Consentement consentement,
            Intervention intervention,
            User currentUser) {
        List<String> missing = new ArrayList<>();

        if (intervention == null) {
            missing.add("planned intervention");
        }
        if (isBlank(entity.getAsaCode())) {
            missing.add("ASA class");
        }
        if (isBlank(entity.getTypeAnesthesie())) {
            missing.add("planned anesthesia type");
        }
        if (!Boolean.TRUE.equals(entity.getJeuneConfirme())) {
            missing.add("fasting confirmation");
        }
        if (!Boolean.TRUE.equals(entity.getConsentementEclaireObtenu())) {
            missing.add("informed consent confirmation");
        }
        if (isBlank(entity.getStrategieAnesthesique())) {
            missing.add("anesthesia strategy");
        }
        if (currentUser == null) {
            missing.add("validator identity");
        }

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Formal medical validation requires: " + String.join(", ", missing)
            );
        }
    }

    private void validateClinicalRanges(ConsultationPreAnesthesique entity) {
        if (entity.getSpo2() != null && (entity.getSpo2() < 80 || entity.getSpo2() > 100)) {
            throw new IllegalArgumentException("SpO2 must be between 80 and 100");
        }

        if (entity.getTemperatureDixieme() != null
                && (entity.getTemperatureDixieme() < 340 || entity.getTemperatureDixieme() > 420)) {
            throw new IllegalArgumentException("Temperature must be between 34.0C and 42.0C");
        }

        if (entity.getFrequenceCardiaque() != null
                && (entity.getFrequenceCardiaque() < 30 || entity.getFrequenceCardiaque() > 200)) {
            throw new IllegalArgumentException("Heart rate must be between 30 and 200 bpm");
        }
    }

    private void applyRiskScore(ConsultationPreAnesthesique entity) {
        int score = 0;
        List<String> factors = new ArrayList<>();

        int asaWeight = resolveAsaWeight(entity.getAsaCode());
        score += asaWeight;
        if (asaWeight >= 4) {
            factors.add("ASA high");
        }

        if (Boolean.TRUE.equals(entity.getUrgence())) {
            score += 2;
            factors.add("Urgent context");
        }

        int mallampatiWeight = resolveMallampatiWeight(entity.getMallampatiCode());
        score += mallampatiWeight;
        if (mallampatiWeight >= 2) {
            factors.add("Airway score");
        }

        if (Boolean.TRUE.equals(entity.getVoieAerienneDifficileSuspectee())) {
            score += 2;
            factors.add("Difficult airway suspected");
        }

        int ageWeight = resolveAgeWeight(entity.getPatient() != null ? entity.getPatient().getDateNaissance() : null);
        score += ageWeight;
        if (ageWeight > 0) {
            factors.add("Age");
        }

        if (entity.getSpo2() != null && entity.getSpo2() < 95) {
            score += 1;
            factors.add("SpO2 < 95%");
        }

        if (entity.getPaSystolique() != null
                && (entity.getPaSystolique() < 90 || entity.getPaSystolique() > 180)) {
            score += 1;
            factors.add("Abnormal blood pressure");
        }

        if (entity.getFrequenceCardiaque() != null
                && (entity.getFrequenceCardiaque() < 50 || entity.getFrequenceCardiaque() > 110)) {
            score += 1;
            factors.add("Abnormal heart rate");
        }

        entity.setRiskScore(score);
        entity.setRiskLevel(resolveRiskLevel(score));
        entity.setRiskSummary(
                factors.isEmpty()
                        ? "Routine profile"
                        : String.join(", ", factors)
        );
    }

    private int resolveAgeWeight(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }

        int age = Period.between(birthDate, LocalDate.now()).getYears();
        if (age >= 75) {
            return 2;
        }
        if (age >= 60) {
            return 1;
        }
        return 0;
    }

    private int resolveMallampatiWeight(String code) {
        String normalized = normalizeAscii(code).replaceAll("[^0-9IV]", "");
        if (normalized.contains("4") || normalized.contains("IV")) {
            return 3;
        }
        if (normalized.contains("3") || normalized.contains("III")) {
            return 2;
        }
        if (normalized.contains("2") || normalized.contains("II")) {
            return 1;
        }
        return 0;
    }

    private int resolveAsaWeight(String code) {
        String normalized = normalizeAscii(code).replaceAll("[^0-9IV]", "");
        if (normalized.contains("6") || normalized.contains("VI")) {
            return 6;
        }
        if (normalized.contains("5") || normalized.contains("V")) {
            return 5;
        }
        if (normalized.contains("4") || normalized.contains("IV")) {
            return 4;
        }
        if (normalized.contains("3") || normalized.contains("III")) {
            return 3;
        }
        if (normalized.contains("2") || normalized.contains("II")) {
            return 2;
        }
        if (normalized.contains("1") || normalized.contains("I")) {
            return 1;
        }
        return 0;
    }

    private String resolveRiskLevel(int score) {
        if (score >= 9) {
            return "TRES_ELEVE";
        }
        if (score >= 6) {
            return "ELEVE";
        }
        if (score >= 3) {
            return "MODERE";
        }
        return "FAIBLE";
    }

    private String normalizeAsaCode(String value) {
        String normalized = normalizeText(value, 30);
        return normalized == null ? null : normalized.toUpperCase();
    }

    private String normalizeText(String value, int maxLength) {
        if (value == null) {
            return null;
        }

        String normalized = value.trim().replaceAll("\\s+", " ");
        if (normalized.isBlank()) {
            return null;
        }

        return normalized.length() <= maxLength
                ? normalized
                : normalized.substring(0, maxLength);
    }

    private String normalizeAscii(String value) {
        if (value == null) {
            return "";
        }

        return Normalizer.normalize(value, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "")
                .toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isBlank();
    }

    private String textOrDash(String value) {
        return isBlank(value) ? "-" : value.trim();
    }

    private String resolveUserDisplayName(User user) {
        if (user == null) {
            return null;
        }

        String label = ((user.getPrenom() == null ? "" : user.getPrenom().trim()) + " "
                + (user.getNom() == null ? "" : user.getNom().trim())).trim();
        return label.isBlank() ? user.getEmail() : label;
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
