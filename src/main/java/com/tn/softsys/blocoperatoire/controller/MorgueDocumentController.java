package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.morgue.MorgueDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.morgue.MorgueDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.service.MorgueDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/autopsies")
@RequiredArgsConstructor
public class MorgueDocumentController {

    private final MorgueDocumentService service;

    @GetMapping("/{autopsieId}/documents")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public List<MorgueDocumentResponseDTO> list(@PathVariable UUID autopsieId) {
        return service.listByAutopsie(autopsieId);
    }

    @PostMapping(value = "/{autopsieId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public MorgueDocumentResponseDTO upload(
            @PathVariable UUID autopsieId,
            @RequestPart("file") MultipartFile file) {

        return service.upload(autopsieId, file);
    }

    @GetMapping("/documents/{documentId}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public ResponseEntity<Resource> download(@PathVariable UUID documentId) {
        MorgueDocumentDownload file = service.download(documentId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(file.getMimeType()))
                .contentLength(file.getSizeBytes())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getFileName() + "\"")
                .body(file.getResource());
    }

    @DeleteMapping("/documents/{documentId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public void delete(@PathVariable UUID documentId) {
        service.delete(documentId);
    }
}
