package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.oms.*;
import com.tn.softsys.blocoperatoire.service.OmsChecklistReportService;
import com.tn.softsys.blocoperatoire.service.OmsChecklistService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/oms")
@RequiredArgsConstructor
public class OmsChecklistController {

    private final OmsChecklistService service;
    private final OmsChecklistReportService reportService;

    @GetMapping("/sign-in")
    public Page<OmsSignInResponseDTO> getSignIns(
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {
        return service.searchSignIns(interventionId, pageable);
    }

    @PostMapping("/sign-in/{interventionId}")
    public ResponseEntity<OmsSignInResponseDTO> createSignIn(
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsSignInRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveSignIn(interventionId, dto, null));
    }

    @PutMapping("/sign-in/{signInId}/{interventionId}")
    public OmsSignInResponseDTO updateSignIn(
            @PathVariable UUID signInId,
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsSignInRequestDTO dto) {
        return service.saveSignIn(interventionId, dto, signInId);
    }

    @GetMapping("/time-out")
    public Page<OmsTimeOutResponseDTO> getTimeOuts(
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {
        return service.searchTimeOuts(interventionId, pageable);
    }

    @PostMapping("/time-out/{interventionId}")
    public ResponseEntity<OmsTimeOutResponseDTO> createTimeOut(
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsTimeOutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveTimeOut(interventionId, dto, null));
    }

    @PutMapping("/time-out/{timeOutId}/{interventionId}")
    public OmsTimeOutResponseDTO updateTimeOut(
            @PathVariable UUID timeOutId,
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsTimeOutRequestDTO dto) {
        return service.saveTimeOut(interventionId, dto, timeOutId);
    }

    @GetMapping("/sign-out")
    public Page<OmsSignOutResponseDTO> getSignOuts(
            @RequestParam(required = false) UUID interventionId,
            Pageable pageable) {
        return service.searchSignOuts(interventionId, pageable);
    }

    @PostMapping("/sign-out/{interventionId}")
    public ResponseEntity<OmsSignOutResponseDTO> createSignOut(
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsSignOutRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.saveSignOut(interventionId, dto, null));
    }

    @PutMapping("/sign-out/{signOutId}/{interventionId}")
    public OmsSignOutResponseDTO updateSignOut(
            @PathVariable UUID signOutId,
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsSignOutRequestDTO dto) {
        return service.saveSignOut(interventionId, dto, signOutId);
    }

    @PostMapping("/sign-out/{interventionId}/verify-credentials")
    public OmsSignOutCredentialVerificationResponseDTO verifySignOutCredentials(
            @PathVariable UUID interventionId,
            @Valid @RequestBody OmsSignOutCredentialVerificationRequestDTO dto) {
        return service.verifySignOutCredentials(interventionId, dto);
    }

    @GetMapping("/{interventionId}/report")
    public ResponseEntity<Resource> downloadChecklistReport(@PathVariable UUID interventionId) {
        OmsChecklistReportDownload report = reportService.download(interventionId);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(report.getContent().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .body(new ByteArrayResource(report.getContent()));
    }
}
