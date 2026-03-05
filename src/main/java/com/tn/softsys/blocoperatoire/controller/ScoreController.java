package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.domain.scores.glasgow.*;
import com.tn.softsys.blocoperatoire.domain.scores.eva.*;
import com.tn.softsys.blocoperatoire.domain.scores.aldrete.*;
import com.tn.softsys.blocoperatoire.domain.scores.apache.*;
import com.tn.softsys.blocoperatoire.domain.scores.sofa.*;
import com.tn.softsys.blocoperatoire.domain.scores.asa.*;

import com.tn.softsys.blocoperatoire.dto.scores.*;
import com.tn.softsys.blocoperatoire.dto.scores.gcs.*;
import com.tn.softsys.blocoperatoire.dto.scores.eva.*;
import com.tn.softsys.blocoperatoire.dto.scores.aldrete.*;
import com.tn.softsys.blocoperatoire.dto.scores.apache.*;
import com.tn.softsys.blocoperatoire.dto.scores.sofa.*;
import com.tn.softsys.blocoperatoire.dto.scores.asa.*;

import com.tn.softsys.blocoperatoire.mapper.scores.*;
import com.tn.softsys.blocoperatoire.service.ScoreService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/scores")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private final HttpServletRequest request;

    /* ======================= GCS ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/gcs")
    public ResponseEntity<GcsResponseDTO> createGcs(
            @Valid @RequestBody GcsRequestDTO dto) {

        String ip = request.getRemoteAddr();

        GcsInput input = new GcsInput(
                dto.getEyes(),
                dto.getVerbal(),
                dto.getMotor(),
                dto.getJustification()
        );

        Glasgow entity = scoreService.createGcs(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GcsMapper.toResponse(entity));
    }

    /* ======================= EVA ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/eva")
    public ResponseEntity<EvaResponseDTO> createEva(
            @Valid @RequestBody EvaRequestDTO dto) {

        String ip = request.getRemoteAddr();

        EvaInput input = new EvaInput(
                dto.getValue(),
                dto.getJustification()
        );

        Eva entity = scoreService.createEva(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EvaMapper.toResponse(entity, dto.getValue() >= 7));
    }

    /* ======================= ALDRETE ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/aldrete")
    public ResponseEntity<AldreteResponseDTO> createAldrete(
            @Valid @RequestBody AldreteRequestDTO dto) {

        String ip = request.getRemoteAddr();

        AldreteInput input = new AldreteInput(
                dto.getActivity(),
                dto.getRespiration(),
                dto.getCirculation(),
                dto.getConsciousness(),
                dto.getOxygenation(),
                dto.getJustification()
        );

        Aldrete entity = scoreService.createAldrete(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AldreteMapper.toResponse(entity));
    }

    /* ======================= APACHE ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/apache")
    public ResponseEntity<ApacheResponseDTO> createApache(
            @Valid @RequestBody ApacheRequestDTO dto) {

        String ip = request.getRemoteAddr();

        ApacheInput input = new ApacheInput(
                dto.getTemperature(),
                dto.getMap(),
                dto.getHeartRate(),
                dto.getRespiratoryRate(),
                dto.getPao2(),
                dto.getPh(),
                dto.getSodium(),
                dto.getPotassium(),
                dto.getCreatinine(),
                dto.getHematocrit(),
                dto.getWbc(),
                dto.getGcs(),
                dto.getAge(),
                dto.getJustification(),
                dto.getChronicHealthStatus()
        );

        ApacheII entity = scoreService.createApache(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApacheMapper.toResponse(entity));
    }

    /* ======================= SOFA ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/sofa")
    public ResponseEntity<SofaResponseDTO> createSofa(
            @Valid @RequestBody SofaRequestDTO dto) {

        String ip = request.getRemoteAddr();

        SofaInput input = new SofaInput(
                dto.getPao2(),
                dto.getFio2(),
                dto.getJustification(),
                dto.isMechanicalVentilation(),
                dto.getPlatelets(),
                dto.getBilirubin(),
                dto.getMap(),
                dto.getDopamine(),
                dto.getDobutamine(),
                dto.getEpinephrine(),
                dto.getNorepinephrine(),
                dto.getGcs(),
                dto.getCreatinine(),
                dto.getUrineOutput24h()
        );

        Sofa entity = scoreService.createSofa(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SofaMapper.toResponse(entity));
    }

    /* ======================= ASA ======================= */

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    @PostMapping("/asa")
    public ResponseEntity<AsaResponseDTO> createAsa(
            @Valid @RequestBody AsaRequestDTO dto) {

        String ip = request.getRemoteAddr();

        AsaInput input = new AsaInput(
                dto.isBrainDeadDonor(),
                dto.isMoribund(),
                dto.isLifeThreateningDisease(),
                dto.isSevereSystemicDisease(),
                dto.isMildSystemicDisease(),
                dto.isEmergency(),
                dto.getJustification()
        );

        Asa entity = scoreService.createAsa(input, dto.getInterventionId(), ip);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(AsaMapper.toResponse(entity));
    }

    /* ======================= READ ======================= */

    @GetMapping("/{id}")
    public ResponseEntity<ScoreResponseDTO> getScoreById(@PathVariable UUID id) {

        Score score = scoreService.getScoreById(id);

        return ResponseEntity.ok(ScoreMapper.toResponse(score));
    }

    @GetMapping("/intervention/{id}")
    public ResponseEntity<Page<ScoreResponseDTO>> getScoresByIntervention(
            @PathVariable UUID id,
            Pageable pageable) {

        return ResponseEntity.ok(
                scoreService.getScoresByIntervention(id, pageable)
                        .map(ScoreMapper::toResponse)
        );
    }

    @GetMapping("/patient/{id}")
    public ResponseEntity<Page<ScoreResponseDTO>> getScoresByPatient(
            @PathVariable UUID id,
            Pageable pageable) {

        return ResponseEntity.ok(
                scoreService.getScoresByPatient(id, pageable)
                        .map(ScoreMapper::toResponse)
        );
    }
}