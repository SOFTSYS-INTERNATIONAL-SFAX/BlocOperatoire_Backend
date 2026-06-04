package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.Intervention;
import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.domain.oms.ChecklistOms;
import com.tn.softsys.blocoperatoire.domain.oms.OmsSignIn;
import com.tn.softsys.blocoperatoire.domain.oms.OmsSignOut;
import com.tn.softsys.blocoperatoire.domain.oms.OmsTimeOut;
import com.tn.softsys.blocoperatoire.dto.oms.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.ChecklistOmsRepository;
import com.tn.softsys.blocoperatoire.repository.ConsultationPreAnesthesiqueRepository;
import com.tn.softsys.blocoperatoire.repository.InterventionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OmsChecklistService {

    private final ChecklistOmsRepository checklistOmsRepository;
    private final InterventionRepository interventionRepository;
    private final ConsultationPreAnesthesiqueRepository consultationPreAnesthesiqueRepository;
    private final AuditLogService auditLogService;
    private final AuditContextService auditContextService;
    private final PasswordEncoder passwordEncoder;

    public Page<OmsSignInResponseDTO> searchSignIns(UUID interventionId, Pageable pageable) {
        Page<ChecklistOms> page = interventionId == null
                ? checklistOmsRepository.findBySignInIsNotNull(pageable)
                : checklistOmsRepository.findByIntervention_InterventionIdAndSignInIsNotNull(interventionId, pageable);

        return page.map(this::toSignInResponse);
    }

    public Page<OmsTimeOutResponseDTO> searchTimeOuts(UUID interventionId, Pageable pageable) {
        Page<ChecklistOms> page = interventionId == null
                ? checklistOmsRepository.findByTimeOutIsNotNull(pageable)
                : checklistOmsRepository.findByIntervention_InterventionIdAndTimeOutIsNotNull(interventionId, pageable);

        return page.map(this::toTimeOutResponse);
    }

    public Page<OmsSignOutResponseDTO> searchSignOuts(UUID interventionId, Pageable pageable) {
        Page<ChecklistOms> page = interventionId == null
                ? checklistOmsRepository.findBySignOutIsNotNull(pageable)
                : checklistOmsRepository.findByIntervention_InterventionIdAndSignOutIsNotNull(interventionId, pageable);

        return page.map(this::toSignOutResponse);
    }

    public OmsSignInResponseDTO saveSignIn(UUID interventionId, OmsSignInRequestDTO dto, UUID signInId) {
        validateSignIn(dto);

        ChecklistOms checklist = findOrCreateChecklist(interventionId);
        ensureSignInEditable(checklist);
        OmsSignIn signIn = checklist.getSignIn();
        boolean isCreate = signIn == null;

        if (signInId != null && (signIn == null || !signIn.getSignInId().equals(signInId))) {
            throw new ResourceNotFoundException("OMS Sign In not found");
        }

        if (signIn == null) {
            signIn = new OmsSignIn();
        }

        User currentUser = getCurrentUserStrict();

        signIn.setPatientIdentityConfirmed(dto.getPatientIdentityConfirmed());
        signIn.setSiteMarked(dto.getSiteMarked());
        signIn.setAnesthesiaMachineChecked(dto.getAnesthesiaMachineChecked());
        signIn.setPulseOximeterWorking(dto.getPulseOximeterWorking());
        signIn.setDifficultAirwayRisk(dto.getDifficultAirwayRisk());
        signIn.setAspirationRisk(dto.getAspirationRisk());
        signIn.setHemorrhageRisk(dto.getHemorrhageRisk());
        signIn.setBloodProductsAvailable(dto.getBloodProductsAvailable());
        signIn.setAllergies(resolveAllergies(dto.getAllergies(), checklist.getPatient()));
        signIn.setCompletedBy(currentUser);
        signIn.setCompletedAt(LocalDateTime.now());

        checklist.setSignIn(signIn);
        checklist = checklistOmsRepository.save(checklist);

        OmsSignIn saved = checklist.getSignIn();

        audit(
                isCreate ? "OMS_SIGN_IN_CREATE" : "OMS_SIGN_IN_UPDATE",
                "OMS_SIGN_IN",
                saved.getSignInId(),
                "Intervention=" + checklist.getIntervention().getInterventionId()
                        + " | Patient=" + checklist.getPatient().getPatientId()
                        + " | Allergies=" + textOrDash(saved.getAllergies())
        );
        audit(
                "OMS_SIGN_IN_VALIDATED",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Sign In valide par " + buildUserLabel(saved.getCompletedBy())
                        + " a " + saved.getCompletedAt()
                        + " | Intervention=" + checklist.getIntervention().getInterventionId()
        );

        return toSignInResponse(checklist);
    }

    public OmsTimeOutResponseDTO saveTimeOut(UUID interventionId, OmsTimeOutRequestDTO dto, UUID timeOutId) {
        validateTimeOut(dto);

        ChecklistOms checklist = findOrCreateChecklist(interventionId);
        ensureTimeOutEditable(checklist);
        OmsTimeOut timeOut = checklist.getTimeOut();
        boolean isCreate = timeOut == null;

        if (timeOutId != null && (timeOut == null || !timeOut.getTimeOutId().equals(timeOutId))) {
            throw new ResourceNotFoundException("OMS Time Out not found");
        }

        if (timeOut == null) {
            timeOut = new OmsTimeOut();
        }

        User currentUser = getCurrentUserStrict();

        timeOut.setTeamIntroduced(dto.getTeamIntroduced());
        timeOut.setPatientNameConfirmed(dto.getPatientNameConfirmed());
        timeOut.setInterventionConfirmed(dto.getInterventionConfirmed());
        timeOut.setSiteConfirmed(dto.getSiteConfirmed());
        timeOut.setAntibioticProphylaxisGiven(dto.getAntibioticProphylaxisGiven());
        timeOut.setImagingDisplayed(dto.getImagingDisplayed());
        timeOut.setCriticalEventsSurgeon(normalizeOptionalText(dto.getCriticalEventsSurgeon()));
        timeOut.setCriticalEventsAnesthesia(normalizeOptionalText(dto.getCriticalEventsAnesthesia()));
        timeOut.setCompletedBy(currentUser);
        timeOut.setCompletedAt(LocalDateTime.now());

        checklist.setTimeOut(timeOut);
        checklist = checklistOmsRepository.save(checklist);

        OmsTimeOut saved = checklist.getTimeOut();

        audit(
                isCreate ? "OMS_TIME_OUT_CREATE" : "OMS_TIME_OUT_UPDATE",
                "OMS_TIME_OUT",
                saved.getTimeOutId(),
                "Intervention=" + checklist.getIntervention().getInterventionId()
                        + " | Patient=" + checklist.getPatient().getPatientId()
                        + " | Antibioprophylaxie=" + saved.getAntibioticProphylaxisGiven()
                        + " | Imagerie=" + saved.getImagingDisplayed()
        );
        audit(
                "OMS_TIME_OUT_VALIDATED",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Time Out valide par " + buildUserLabel(saved.getCompletedBy())
                        + " a " + saved.getCompletedAt()
                        + " | Intervention=" + checklist.getIntervention().getInterventionId()
        );

        return toTimeOutResponse(checklist);
    }

    public OmsSignOutResponseDTO saveSignOut(UUID interventionId, OmsSignOutRequestDTO dto, UUID signOutId) {
        ChecklistOms checklist = findOrCreateChecklist(interventionId);
        ensureSignOutEditable(checklist);
        validateSignOut(dto, checklist);
        OmsSignOut signOut = checklist.getSignOut();
        boolean isCreate = signOut == null;

        if (signOutId != null && (signOut == null || !signOut.getSignOutId().equals(signOutId))) {
            throw new ResourceNotFoundException("OMS Sign Out not found");
        }

        if (signOut == null) {
            signOut = new OmsSignOut();
        }

        User currentUser = getCurrentUserStrict();

        signOut.setInterventionRecorded(dto.getInterventionRecorded());
        signOut.setInstrumentsCountCorrect(dto.getInstrumentsCountCorrect());
        signOut.setSpecimensLabeled(dto.getSpecimensLabeled());
        signOut.setRecoveryPlanConfirmed(dto.getRecoveryPlanConfirmed());
        signOut.setEquipmentProblems(StringUtils.hasText(dto.getEquipmentProblems()) ? dto.getEquipmentProblems().trim() : null);
        signOut.setSurgeonValidated(dto.getSurgeonValidated());
        signOut.setAnesthesisteValidated(dto.getAnesthesisteValidated());
        signOut.setSurgeonValidatedByName(resolveAssignedUserLabel(checklist.getIntervention().getChirurgien(), "Chirurgien non attribue"));
        signOut.setAnesthesisteValidatedByName(resolveAnesthesisteValidationLabel(checklist));
        LocalDateTime validationTime = LocalDateTime.now();
        signOut.setSurgeonValidatedAt(Boolean.TRUE.equals(dto.getSurgeonValidated()) ? validationTime : null);
        signOut.setAnesthesisteValidatedAt(Boolean.TRUE.equals(dto.getAnesthesisteValidated()) ? validationTime : null);
        signOut.setInfirmierValidated(null);
        signOut.setInfirmierValidatedByName(null);
        signOut.setInfirmierValidatedAt(null);
        signOut.setCompletedBy(currentUser);
        signOut.setCompletedAt(validationTime);

        checklist.setSignOut(signOut);
        checklist = checklistOmsRepository.save(checklist);

        OmsSignOut saved = checklist.getSignOut();

        audit(
                isCreate ? "OMS_SIGN_OUT_CREATE" : "OMS_SIGN_OUT_UPDATE",
                "OMS_SIGN_OUT",
                saved.getSignOutId(),
                "Intervention=" + checklist.getIntervention().getInterventionId()
                        + " | Patient=" + checklist.getPatient().getPatientId()
                        + " | ProblemeMateriel=" + textOrDash(saved.getEquipmentProblems())
        );
        audit(
                "OMS_SIGN_OUT_SURGEON_SIGNED",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Sign Out chirurgien confirme par " + textOrDash(saved.getSurgeonValidatedByName())
                        + " a " + saved.getSurgeonValidatedAt()
        );
        audit(
                "OMS_SIGN_OUT_ANESTHESISTE_SIGNED",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Sign Out anesthesiste confirme par " + textOrDash(saved.getAnesthesisteValidatedByName())
                        + " a " + saved.getAnesthesisteValidatedAt()
        );
        audit(
                "OMS_SIGN_OUT_VALIDATED",
                "OMS_CHECKLIST",
                checklist.getChecklistId(),
                "Sign Out final valide par " + buildUserLabel(saved.getCompletedBy())
                        + " a " + saved.getCompletedAt()
                        + " | Intervention=" + checklist.getIntervention().getInterventionId()
        );

        return toSignOutResponse(checklist);
    }

    public OmsSignOutCredentialVerificationResponseDTO verifySignOutCredentials(
            UUID interventionId,
            OmsSignOutCredentialVerificationRequestDTO dto
    ) {
        ChecklistOms checklist = resolveChecklistContext(interventionId);
        User surgeon = checklist.getIntervention() != null ? checklist.getIntervention().getChirurgien() : null;
        User anesthesiste = resolveAnesthesisteValidationUser(checklist);

        boolean surgeonProvided = StringUtils.hasText(dto.getSurgeonPassword());
        boolean anesthesisteProvided = StringUtils.hasText(dto.getAnesthesistePassword());

        String surgeonMessage = surgeonProvided
                ? validateAssignedUserPasswordForVerification(
                        surgeon,
                        dto.getSurgeonPassword(),
                        "chirurgien"
                )
                : null;
        String anesthesisteMessage = anesthesisteProvided
                ? validateAssignedUserPasswordForVerification(
                        anesthesiste,
                        dto.getAnesthesistePassword(),
                        "anesthesiste"
                )
                : null;

        return OmsSignOutCredentialVerificationResponseDTO.builder()
                .surgeonVerified(surgeonProvided && !StringUtils.hasText(surgeonMessage))
                .anesthesisteVerified(anesthesisteProvided && !StringUtils.hasText(anesthesisteMessage))
                .surgeonMessage(surgeonMessage)
                .anesthesisteMessage(anesthesisteMessage)
                .surgeonLabel(resolveAssignedUserLabel(surgeon, "Chirurgien non attribue"))
                .anesthesisteLabel(resolveAnesthesisteValidationLabel(checklist))
                .build();
    }

    private ChecklistOms findOrCreateChecklist(UUID interventionId) {
        return checklistOmsRepository.findByIntervention_InterventionId(interventionId)
                .orElseGet(() -> {
                    Intervention intervention = interventionRepository.findById(interventionId)
                            .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

                    return ChecklistOms.builder()
                            .intervention(intervention)
                            .patient(intervention.getPatient())
                            .build();
                });
    }

    private ChecklistOms resolveChecklistContext(UUID interventionId) {
        ChecklistOms existing = checklistOmsRepository.findByIntervention_InterventionId(interventionId)
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        Intervention intervention = interventionRepository.findById(interventionId)
                .orElseThrow(() -> new ResourceNotFoundException("Intervention not found"));

        return ChecklistOms.builder()
                .intervention(intervention)
                .patient(intervention.getPatient())
                .build();
    }

    private void validateSignIn(OmsSignInRequestDTO dto) {
        requireTrue(dto.getPatientIdentityConfirmed(), "patientIdentityConfirmed");
        requireTrue(dto.getSiteMarked(), "siteMarked");
        requireTrue(dto.getAnesthesiaMachineChecked(), "anesthesiaMachineChecked");
        requireTrue(dto.getPulseOximeterWorking(), "pulseOximeterWorking");
    }

    private void validateTimeOut(OmsTimeOutRequestDTO dto) {
        requireTrue(dto.getTeamIntroduced(), "teamIntroduced");
        requireTrue(dto.getPatientNameConfirmed(), "patientNameConfirmed");
        requireTrue(dto.getInterventionConfirmed(), "interventionConfirmed");
        requireTrue(dto.getSiteConfirmed(), "siteConfirmed");
    }

    private void validateSignOut(OmsSignOutRequestDTO dto, ChecklistOms checklist) {
        requireTrue(dto.getInterventionRecorded(), "interventionRecorded");
        requireTrue(dto.getInstrumentsCountCorrect(), "instrumentsCountCorrect");
        requireTrue(dto.getSpecimensLabeled(), "specimensLabeled");
        requireTrue(dto.getRecoveryPlanConfirmed(), "recoveryPlanConfirmed");
        requireTrue(dto.getSurgeonValidated(), "surgeonValidated");
        requireTrue(dto.getAnesthesisteValidated(), "anesthesisteValidated");
        validateAssignedUserPassword(
                checklist.getIntervention() != null ? checklist.getIntervention().getChirurgien() : null,
                dto.getSurgeonPassword(),
                "chirurgien"
        );
        validateAssignedUserPassword(
                resolveAnesthesisteValidationUser(checklist),
                dto.getAnesthesistePassword(),
                "anesthesiste"
        );
    }

    private void ensureSignInEditable(ChecklistOms checklist) {
        if (checklist.getTimeOut() != null || checklist.getSignOut() != null) {
            throw new IllegalStateException("OMS Sign In is locked after the next checklist step has started");
        }
    }

    private void ensureTimeOutEditable(ChecklistOms checklist) {
        if (checklist.getSignIn() == null) {
            throw new IllegalStateException("OMS Time Out requires a validated Sign In first");
        }
        if (checklist.getSignOut() != null) {
            throw new IllegalStateException("OMS Time Out is locked after Sign Out");
        }
    }

    private void ensureSignOutEditable(ChecklistOms checklist) {
        if (checklist.getTimeOut() == null) {
            throw new IllegalStateException("OMS Sign Out requires a validated Time Out first");
        }
        if (checklist.getSignOut() != null) {
            throw new IllegalStateException("OMS Sign Out is final and cannot be modified");
        }
    }

    private void requireTrue(Boolean value, String fieldName) {
        if (!Boolean.TRUE.equals(value)) {
            throw new IllegalArgumentException(fieldName + " must be true");
        }
    }

    private String resolveAllergies(String requestValue, Patient patient) {
        if (StringUtils.hasText(requestValue)) {
            return requestValue.trim();
        }

        if (patient.getAllergies() == null || patient.getAllergies().isEmpty()) {
            return null;
        }

        return patient.getAllergies().stream()
                .filter(StringUtils::hasText)
                .map(String::trim)
                .collect(Collectors.joining(", "));
    }

    private String normalizeOptionalText(String value) {
        return StringUtils.hasText(value) ? value.trim() : null;
    }

    private User getCurrentUserStrict() {
        User user = auditContextService.getCurrentUserOrNull();

        if (user == null) {
            throw new IllegalStateException("No authenticated user found");
        }

        return user;
    }

    private void audit(String action, String module, UUID referenceId, String details) {
        auditLogService.log(
                getCurrentUserStrict(),
                action,
                module,
                referenceId,
                details,
                auditContextService.getClientIp()
        );
    }

    private String textOrDash(String value) {
        return StringUtils.hasText(value) ? value.trim() : "-";
    }

    private void validateAssignedUserPassword(User user, String rawPassword, String label) {
        if (user == null) {
            throw new IllegalStateException("Aucun " + label + " assigne a cette intervention");
        }

        if (!StringUtils.hasText(rawPassword)) {
            throw new IllegalArgumentException("Le mot de passe du " + label + " est obligatoire");
        }

        if (!Boolean.TRUE.equals(user.getEnabled()) || !Boolean.TRUE.equals(user.getAccountNonLocked())) {
            throw new IllegalStateException("Le compte du " + label + " assigne est inactif");
        }

        if (!passwordEncoder.matches(rawPassword.trim(), user.getPassword())) {
            throw new IllegalArgumentException("Mot de passe " + label + " incorrect");
        }
    }

    private String validateAssignedUserPasswordForVerification(User user, String rawPassword, String label) {
        try {
            validateAssignedUserPassword(user, rawPassword, label);
            return null;
        } catch (IllegalArgumentException | IllegalStateException ex) {
            return ex.getMessage();
        }
    }

    private String resolveAssignedUserLabel(User user, String fallback) {
        if (user == null) {
            return fallback;
        }
        return buildUserLabel(user);
    }

    private User resolveAnesthesisteValidationUser(ChecklistOms checklist) {
        if (checklist.getIntervention() != null && checklist.getIntervention().getInterventionId() != null) {
            var consultation = consultationPreAnesthesiqueRepository
                    .findFirstByIntervention_InterventionIdOrderByUpdatedAtDesc(
                            checklist.getIntervention().getInterventionId()
                    )
                    .orElse(null);

            if (consultation != null && consultation.getAnesthesiste() != null) {
                return consultation.getAnesthesiste();
            }
        }

        return checklist.getIntervention() != null ? checklist.getIntervention().getAnesthesiste() : null;
    }

    private String resolveAnesthesisteValidationLabel(ChecklistOms checklist) {
        if (checklist.getIntervention() != null && checklist.getIntervention().getInterventionId() != null) {
            var consultation = consultationPreAnesthesiqueRepository
                    .findFirstByIntervention_InterventionIdOrderByUpdatedAtDesc(
                            checklist.getIntervention().getInterventionId()
                    )
                    .orElse(null);

            if (consultation != null) {
                String consultationName = consultation.getAnesthesiste() != null
                        ? buildUserLabel(consultation.getAnesthesiste())
                        : textOrDash(consultation.getMedecinNom());

                if (!"-".equals(consultationName)) {
                    return consultationName;
                }
            }
        }

        return resolveAssignedUserLabel(
                checklist.getIntervention() != null ? checklist.getIntervention().getAnesthesiste() : null,
                "Anesthesiste non attribue"
        );
    }

    private String buildUserLabel(User user) {
        String fullName = ((user.getPrenom() != null ? user.getPrenom() : "") + " " +
                (user.getNom() != null ? user.getNom() : "")).trim();

        return fullName.isBlank() ? user.getEmail() : "Dr. " + fullName;
    }

    private OmsSignInResponseDTO toSignInResponse(ChecklistOms checklist) {
        OmsSignIn signIn = checklist.getSignIn();

        return OmsSignInResponseDTO.builder()
                .checklistId(checklist.getChecklistId())
                .signInId(signIn.getSignInId())
                .patientId(checklist.getPatient().getPatientId())
                .interventionId(checklist.getIntervention().getInterventionId())
                .patientIdentityConfirmed(signIn.getPatientIdentityConfirmed())
                .siteMarked(signIn.getSiteMarked())
                .anesthesiaMachineChecked(signIn.getAnesthesiaMachineChecked())
                .pulseOximeterWorking(signIn.getPulseOximeterWorking())
                .difficultAirwayRisk(signIn.getDifficultAirwayRisk())
                .aspirationRisk(signIn.getAspirationRisk())
                .hemorrhageRisk(signIn.getHemorrhageRisk())
                .bloodProductsAvailable(signIn.getBloodProductsAvailable())
                .allergies(signIn.getAllergies())
                .completedByUserId(signIn.getCompletedBy().getUserId())
                .completedByName(buildUserLabel(signIn.getCompletedBy()))
                .completedAt(signIn.getCompletedAt())
                .build();
    }

    private OmsTimeOutResponseDTO toTimeOutResponse(ChecklistOms checklist) {
        OmsTimeOut timeOut = checklist.getTimeOut();

        return OmsTimeOutResponseDTO.builder()
                .checklistId(checklist.getChecklistId())
                .timeOutId(timeOut.getTimeOutId())
                .patientId(checklist.getPatient().getPatientId())
                .interventionId(checklist.getIntervention().getInterventionId())
                .teamIntroduced(timeOut.getTeamIntroduced())
                .patientNameConfirmed(timeOut.getPatientNameConfirmed())
                .interventionConfirmed(timeOut.getInterventionConfirmed())
                .siteConfirmed(timeOut.getSiteConfirmed())
                .antibioticProphylaxisGiven(timeOut.getAntibioticProphylaxisGiven())
                .imagingDisplayed(timeOut.getImagingDisplayed())
                .criticalEventsSurgeon(timeOut.getCriticalEventsSurgeon())
                .criticalEventsAnesthesia(timeOut.getCriticalEventsAnesthesia())
                .completedByUserId(timeOut.getCompletedBy().getUserId())
                .completedByName(buildUserLabel(timeOut.getCompletedBy()))
                .completedAt(timeOut.getCompletedAt())
                .build();
    }

    private OmsSignOutResponseDTO toSignOutResponse(ChecklistOms checklist) {
        OmsSignOut signOut = checklist.getSignOut();

        return OmsSignOutResponseDTO.builder()
                .checklistId(checklist.getChecklistId())
                .signOutId(signOut.getSignOutId())
                .patientId(checklist.getPatient().getPatientId())
                .interventionId(checklist.getIntervention().getInterventionId())
                .interventionRecorded(signOut.getInterventionRecorded())
                .instrumentsCountCorrect(signOut.getInstrumentsCountCorrect())
                .specimensLabeled(signOut.getSpecimensLabeled())
                .recoveryPlanConfirmed(signOut.getRecoveryPlanConfirmed())
                .equipmentProblems(signOut.getEquipmentProblems())
                .surgeonValidated(signOut.getSurgeonValidated())
                .anesthesisteValidated(signOut.getAnesthesisteValidated())
                .infirmierValidated(signOut.getInfirmierValidated())
                .surgeonValidatedByName(signOut.getSurgeonValidatedByName())
                .anesthesisteValidatedByName(signOut.getAnesthesisteValidatedByName())
                .infirmierValidatedByName(signOut.getInfirmierValidatedByName())
                .surgeonValidatedAt(signOut.getSurgeonValidatedAt())
                .anesthesisteValidatedAt(signOut.getAnesthesisteValidatedAt())
                .infirmierValidatedAt(signOut.getInfirmierValidatedAt())
                .completedByUserId(signOut.getCompletedBy().getUserId())
                .completedByName(buildUserLabel(signOut.getCompletedBy()))
                .completedAt(signOut.getCompletedAt())
                .build();
    }
}
