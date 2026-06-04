package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.PreAnesthesieDocumentCategory;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.preanesthesie.PreAnesthesieDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.service.PreAnesthesieDocumentService;
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
@RequestMapping("/api/pre-anesthesie")
@RequiredArgsConstructor
public class PreAnesthesieDocumentController {

    private final PreAnesthesieDocumentService service;

    @GetMapping("/{consultationId}/documents")
    public List<PreAnesthesieDocumentResponseDTO> list(@PathVariable UUID consultationId) {
        return service.listByConsultation(consultationId);
    }

    @PostMapping(value = "/{consultationId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public PreAnesthesieDocumentResponseDTO upload(
            @PathVariable UUID consultationId,
            @RequestParam("title") String title,
            @RequestParam("category") PreAnesthesieDocumentCategory category,
            @RequestParam("file") MultipartFile file) {
        return service.upload(consultationId, title, category, file);
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID documentId) {
        PreAnesthesieDocumentDownload file = service.download(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getSizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getResource());
    }

    @DeleteMapping("/documents/{documentId}")
    public void delete(@PathVariable UUID documentId) {
        service.delete(documentId);
    }
}
