package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.PatientDocumentCategory;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDocumentDownload;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDocumentResponseDTO;
import com.tn.softsys.blocoperatoire.service.PatientDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientDocumentController {

    private final PatientDocumentService service;

    @GetMapping("/{patientId}/documents")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public List<PatientDocumentResponseDTO> list(@PathVariable UUID patientId) {
        return service.listByPatient(patientId);
    }

    @PostMapping(value = "/{patientId}/documents", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public PatientDocumentResponseDTO upload(
            @PathVariable UUID patientId,
            @RequestParam("title") String title,
            @RequestParam("category") PatientDocumentCategory category,
            @RequestParam("file") MultipartFile file) {
        return service.upload(patientId, title, category, file);
    }

    @GetMapping("/documents/{documentId}/download")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public ResponseEntity<Resource> download(@PathVariable UUID documentId) {
        PatientDocumentDownload file = service.download(documentId);

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
