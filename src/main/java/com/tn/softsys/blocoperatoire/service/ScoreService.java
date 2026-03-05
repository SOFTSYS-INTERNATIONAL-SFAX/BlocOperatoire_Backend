package com.tn.softsys.blocoperatoire.service;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.domain.scores.*;
import com.tn.softsys.blocoperatoire.domain.scores.glasgow.*;
import com.tn.softsys.blocoperatoire.domain.scores.eva.*;
import com.tn.softsys.blocoperatoire.domain.scores.aldrete.*;
import com.tn.softsys.blocoperatoire.domain.scores.apache.*;
import com.tn.softsys.blocoperatoire.domain.scores.sofa.*;
import com.tn.softsys.blocoperatoire.domain.scores.asa.*;
import com.tn.softsys.blocoperatoire.exception.ResourceNotFoundException;
import com.tn.softsys.blocoperatoire.repository.*;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ScoreService {

    private final GlasgowRepository glasgowRepository;
    private final EvaRepository evaRepository;
    private final AldreteRepository aldreteRepository;
    private final ApacheRepository apacheRepository;
    private final SofaRepository sofaRepository;
    private final AsaRepository asaRepository;

    private final ScoreRepository scoreRepository;
    private final InterventionRepository interventionRepository;
    private final UserRepository userRepository;

    private final AuditLogService auditLogService;

    private final GcsCalculator gcsCalculator;
    private final EvaCalculator evaCalculator;
    private final AldreteCalculator aldreteCalculator;
    private final ApacheIICalculator apacheCalculator;
    private final SofaCalculator sofaCalculator;
    private final AsaCalculator asaCalculator;

    /* ===================================================== */
    /* ======================= CREATE ====================== */
    /* ===================================================== */

    public Glasgow createGcs(GcsInput input,
                             UUID interventionId,
                             String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        GcsResult result = gcsCalculator.calculate(input);

        Glasgow entity = new Glasgow();
        entity.setEyes(input.getEyes());
        entity.setVerbal(input.getVerbal());
        entity.setMoteur(input.getMotor());
        entity.setValeur(result.total());
        entity.setGravite(result.gravite());
        entity.setAlgorithmVersion(gcsCalculator.getVersion());
        entity.setScoreType(ScoreType.GCS);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        Glasgow saved = glasgowRepository.save(entity);

        audit("CREATE_SCORE", "GCS", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    public Eva createEva(EvaInput input,
                         UUID interventionId,
                         String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        EvaResult result = evaCalculator.calculate(input);

        Eva entity = new Eva();
        entity.setValue(result.value());
        entity.setValeur(result.value());
        entity.setAlertLevel(result.alertLevel());
        entity.setAlgorithmVersion(evaCalculator.getVersion());
        entity.setScoreType(ScoreType.EVA);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        Eva saved = evaRepository.save(entity);

        audit("CREATE_SCORE", "EVA", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    public Aldrete createAldrete(AldreteInput input,
                                 UUID interventionId,
                                 String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        AldreteResult result = aldreteCalculator.calculate(input);

        Aldrete entity = new Aldrete();
        entity.setActivity(input.getActivity());
        entity.setRespiration(input.getRespiration());
        entity.setCirculation(input.getCirculation());
        entity.setConsciousness(input.getConsciousness());
        entity.setOxygenation(input.getOxygenation());
        entity.setDecision(result.decision());
        entity.setValeur(result.total());
        entity.setAlgorithmVersion(aldreteCalculator.getVersion());
        entity.setScoreType(ScoreType.ALDRETE);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        Aldrete saved = aldreteRepository.save(entity);

        audit("CREATE_SCORE", "ALDRETE", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    public ApacheII createApache(ApacheInput input,
                                 UUID interventionId,
                                 String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        ApacheResult result = apacheCalculator.calculate(input);

        ApacheII entity = new ApacheII();
        entity.setTemperature(input.getTemperature());
        entity.setMap(input.getMap());
        entity.setHeartRate(input.getHeartRate());
        entity.setRespiratoryRate(input.getRespiratoryRate());
        entity.setPao2(input.getPao2());
        entity.setPh(input.getPh());
        entity.setSodium(input.getSodium());
        entity.setPotassium(input.getPotassium());
        entity.setCreatinine(input.getCreatinine());
        entity.setHematocrit(input.getHematocrit());
        entity.setWbc(input.getWbc());
        entity.setGcs(input.getGcs());
        entity.setAge(input.getAge());
        entity.setChronicHealthStatus(input.getChronicHealthStatus());

        entity.setValeur(result.totalScore());
        entity.setMortalityProbability(result.mortalityProbability());
        entity.setAlgorithmVersion(apacheCalculator.getVersion());
        entity.setScoreType(ScoreType.APACHEII);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        ApacheII saved = apacheRepository.save(entity);

        audit("CREATE_SCORE", "APACHEII", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    public Sofa createSofa(SofaInput input,
                           UUID interventionId,
                           String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        SofaResult result = sofaCalculator.calculate(input);

        Sofa entity = new Sofa();
        entity.setRespiratoryScore(result.respiratoryScore());
        entity.setCoagulationScore(result.coagulationScore());
        entity.setLiverScore(result.liverScore());
        entity.setCardiovascularScore(result.cardiovascularScore());
        entity.setNeurologicalScore(result.neurologicalScore());
        entity.setRenalScore(result.renalScore());
        entity.setValeur(result.totalScore());
        entity.setSepsisAlert(result.sepsisAlert());
        entity.setAlgorithmVersion(sofaCalculator.getVersion());
        entity.setScoreType(ScoreType.SOFA);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        Sofa saved = sofaRepository.save(entity);

        audit("CREATE_SCORE", "SOFA", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    public Asa createAsa(AsaInput input,
                         UUID interventionId,
                         String ipAddress) {

        Intervention intervention = getIntervention(interventionId);
        validateIntervention(intervention);

        AsaResult result = asaCalculator.calculate(input);

        Asa entity = new Asa();
        entity.setAsaClass(result.asaClass());
        entity.setEmergency(result.emergency());
        entity.setFinalClassification(result.finalClassification());
        entity.setValeur(result.asaClass().ordinal() + 1);
        entity.setAlgorithmVersion(asaCalculator.getVersion());
        entity.setScoreType(ScoreType.ASA);
        entity.setIntervention(intervention);

        enrichMetadata(entity, input.getJustification());

        Asa saved = asaRepository.save(entity);

        audit("CREATE_SCORE", "ASA", saved.getScoreId(), interventionId, ipAddress);

        return saved;
    }

    /* ===================================================== */
    /* ======================== READ ======================= */
    /* ===================================================== */

    @Transactional(readOnly = true)
    public Score getScoreById(UUID scoreId) {

        return scoreRepository.findById(scoreId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Score not found with id: " + scoreId
                        ));
    }

    @Transactional(readOnly = true)
    public Page<Score> getScoresByIntervention(UUID interventionId, Pageable pageable) {

        if (!interventionRepository.existsById(interventionId)) {
            throw new ResourceNotFoundException(
                    "Intervention not found with id: " + interventionId
            );
        }

        return scoreRepository.findByIntervention_InterventionId(
                interventionId,
                pageable
        );
    }

    @Transactional(readOnly = true)
    public Page<Score> getScoresByPatient(UUID patientId, Pageable pageable) {

        return scoreRepository.findByIntervention_Patient_PatientId(
                patientId,
                pageable
        );
    }

    /* ===================================================== */
    /* ====================== PRIVATE ====================== */
    /* ===================================================== */

    private Intervention getIntervention(UUID id) {

        return interventionRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Intervention not found with id: " + id
                        ));
    }

    private void validateIntervention(Intervention intervention) {

        if (intervention.getStatut() == StatutIntervention.CLOTUREE) {
            throw new IllegalStateException(
                    "Cannot add score to closed intervention"
            );
        }
    }

    private void enrichMetadata(Score entity, String justification) {

        entity.setCalculatedBy(getCurrentUserStrict());
        entity.setJustification(justification);
    }

    private User getCurrentUserStrict() {

        Authentication auth =
                SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found in DB"
                        ));
    }

    private void audit(String action,
                       String module,
                       UUID scoreId,
                       UUID interventionId,
                       String ipAddress) {

        auditLogService.log(
                getCurrentUserStrict(),
                action,
                module,
                scoreId,
                "Score ID: " + scoreId +
                        " | Intervention ID: " + interventionId,
                ipAddress
        );
    }
}