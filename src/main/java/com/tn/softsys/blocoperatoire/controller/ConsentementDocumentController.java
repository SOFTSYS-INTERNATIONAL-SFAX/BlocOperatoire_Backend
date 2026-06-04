package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.service.ConsentementDocumentService;
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
@RequestMapping("/api/consentements")
@RequiredArgsConstructor
public class ConsentementDocumentController {

    private final ConsentementDocumentService service;

    @GetMapping("/{consentId}/documents")
    public List<ConsentementDocumentResponseDTO> list(@PathVariable UUID consentId) {
        return service.listByConsentement(consentId);
    }

    @PostMapping(value = "/{consentId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ConsentementDocumentResponseDTO upload(
            @PathVariable UUID consentId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {
        return service.upload(consentId, title, file);
    }

    @GetMapping("/documents/{documentId}/download")
    public ResponseEntity<Resource> download(@PathVariable UUID documentId) {
        ConsentementDocumentDownload file = service.download(documentId);

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
