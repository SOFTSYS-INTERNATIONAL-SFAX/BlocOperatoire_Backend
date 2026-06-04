package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.compterendu.*;
import com.tn.softsys.blocoperatoire.service.CompteRenduService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/compte-rendu")
@RequiredArgsConstructor
public class CompteRenduController {

    private final CompteRenduService service;

    @GetMapping("/templates")
    public List<CompteRenduTemplateResponseDTO> listTemplates() {
        return service.listTemplates();
    }

    @PostMapping("/templates")
    public CompteRenduTemplateResponseDTO createTemplate(@Valid @RequestBody CompteRenduTemplateRequestDTO dto) {
        return service.createTemplate(dto);
    }

    @PutMapping("/templates/{templateId}")
    public CompteRenduTemplateResponseDTO updateTemplate(
            @PathVariable UUID templateId,
            @Valid @RequestBody CompteRenduTemplateRequestDTO dto) {
        return service.updateTemplate(templateId, dto);
    }

    @DeleteMapping("/templates/{templateId}")
    public void deleteTemplate(@PathVariable UUID templateId) {
        service.deleteTemplate(templateId);
    }

    @GetMapping("/patient/{patientId}/documents")
    public List<CompteRenduDocumentResponseDTO> listPatientDocuments(@PathVariable UUID patientId) {
        return service.listDocumentsByPatient(patientId);
    }

    @GetMapping("/documents/{documentId}")
    public CompteRenduDocumentResponseDTO getDocument(@PathVariable UUID documentId) {
        return service.getDocument(documentId);
    }

    @PostMapping("/documents")
    public CompteRenduDocumentResponseDTO createDocument(@Valid @RequestBody CompteRenduDocumentRequestDTO dto) {
        return service.createDocument(dto);
    }

    @PutMapping("/documents/{documentId}")
    public CompteRenduDocumentResponseDTO updateDocument(
            @PathVariable UUID documentId,
            @Valid @RequestBody CompteRenduDocumentRequestDTO dto) {
        return service.updateDocument(documentId, dto);
    }

    @PostMapping(value = "/documents/{documentId}/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public CompteRenduDocumentResponseDTO uploadAudio(
            @PathVariable UUID documentId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "durationSeconds", required = false) Integer durationSeconds,
            @RequestParam(value = "templateId", required = false) UUID templateId) {
        return service.uploadAudio(documentId, file, durationSeconds, templateId);
    }

    @GetMapping("/documents/{documentId}/audio")
    public ResponseEntity<Resource> downloadAudio(@PathVariable UUID documentId) {
        CompteRenduAudioDownload file = service.downloadAudio(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getSizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getResource());
    }
}
