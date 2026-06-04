package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.preanesthesie.ConsultationPreAnesthesiqueRequestDTO;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.ConsultationPreAnesthesiqueResponseDTO;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieReportDownload;
import com.tn.softsys.blocoperatoire.service.ConsultationPreAnesthesiqueReportService;
import com.tn.softsys.blocoperatoire.service.ConsultationPreAnesthesiqueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/pre-anesthesie")
@RequiredArgsConstructor
public class ConsultationPreAnesthesiqueController {

    private final ConsultationPreAnesthesiqueService service;
    private final ConsultationPreAnesthesiqueReportService reportService;

    @PostMapping
    public ConsultationPreAnesthesiqueResponseDTO create(
            @Valid @RequestBody ConsultationPreAnesthesiqueRequestDTO dto) {
        return service.create(dto);
    }

    @GetMapping("/{id}")
    public ConsultationPreAnesthesiqueResponseDTO getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<ConsultationPreAnesthesiqueResponseDTO> search(
            @RequestParam(required = false) UUID patientId,
            Pageable pageable) {
        return service.search(patientId, pageable);
    }

    @PutMapping("/{id}")
    public ConsultationPreAnesthesiqueResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody ConsultationPreAnesthesiqueRequestDTO dto) {
        return service.update(id, dto);
    }

    @GetMapping("/{id}/report")
    public ResponseEntity<Resource> downloadReport(@PathVariable UUID id) {
        PreAnesthesieReportDownload report = reportService.download(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(report.getContent().length)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + report.getFileName() + "\"")
                .body(new ByteArrayResource(report.getContent()));
    }
}
