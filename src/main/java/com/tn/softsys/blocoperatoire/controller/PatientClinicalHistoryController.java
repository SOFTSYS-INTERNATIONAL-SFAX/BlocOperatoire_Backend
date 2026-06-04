package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.dto.patient.PatientClinicalHistoryRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientClinicalHistoryResponseDTO;
import com.tn.softsys.blocoperatoire.service.PatientClinicalHistoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientClinicalHistoryController {

    private final PatientClinicalHistoryService service;

    @GetMapping("/{patientId}/clinical-history")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public List<PatientClinicalHistoryResponseDTO> list(@PathVariable UUID patientId) {
        return service.listByPatient(patientId);
    }

    @PostMapping("/{patientId}/clinical-history")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN','ROLE_INFIRMIER','ROLE_IADE','ROLE_IBODE')")
    public ResponseEntity<PatientClinicalHistoryResponseDTO> create(
            @PathVariable UUID patientId,
            @Valid @RequestBody PatientClinicalHistoryRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(patientId, dto));
    }

    @DeleteMapping("/clinical-history/{entryId}")
    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN','ROLE_MEDECIN')")
    public void delete(@PathVariable UUID entryId) {
        service.delete(entryId);
    }
}
