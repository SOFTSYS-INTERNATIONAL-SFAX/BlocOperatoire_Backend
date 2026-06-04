package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import com.tn.softsys.blocoperatoire.dto.patient.PatientArchiveRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientDuplicateCandidateDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientMergeRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import com.tn.softsys.blocoperatoire.service.PatientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;

    @PostMapping
    public ResponseEntity<PatientResponseDTO> createPatient(@Valid @RequestBody PatientRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.create(dto));
    }

    @GetMapping
    public ResponseEntity<Page<PatientResponseDTO>> getPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.search(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> getPatient(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PatientResponseDTO> updatePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDTO dto) {
        return ResponseEntity.ok(patientService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable UUID id) {
        patientService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/archive")
    public ResponseEntity<PatientResponseDTO> archivePatient(
            @PathVariable UUID id,
            @Valid @RequestBody PatientArchiveRequestDTO dto) {
        return ResponseEntity.ok(patientService.archive(id, dto.getReason()));
    }

    @GetMapping("/archived")
    public ResponseEntity<Page<PatientResponseDTO>> getArchivedPatients(Pageable pageable) {
        return ResponseEntity.ok(patientService.getArchived(pageable));
    }

    @GetMapping("/{id}/duplicates")
    public ResponseEntity<List<PatientDuplicateCandidateDTO>> getDuplicates(@PathVariable UUID id) {
        return ResponseEntity.ok(patientService.listDuplicates(id));
    }

    @PostMapping("/merge")
    public ResponseEntity<PatientResponseDTO> mergePatients(@Valid @RequestBody PatientMergeRequestDTO dto) {
        return ResponseEntity.ok(patientService.merge(dto));
    }
}
