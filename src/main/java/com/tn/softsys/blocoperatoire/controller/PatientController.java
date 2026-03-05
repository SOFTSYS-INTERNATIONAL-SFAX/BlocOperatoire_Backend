package com.tn.softsys.blocoperatoire.controller;

import com.tn.softsys.blocoperatoire.domain.GroupeSanguin;
import com.tn.softsys.blocoperatoire.domain.Sexe;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import com.tn.softsys.blocoperatoire.service.PatientService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService service;

    /* =====================================================
       CREATE
       ===================================================== */

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN','CADRE')")
    public PatientResponseDTO create(
            @Valid @RequestBody PatientRequestDTO dto) {

        return service.create(dto);
    }

    /* =====================================================
       UPDATE
       ===================================================== */

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public PatientResponseDTO update(
            @PathVariable UUID id,
            @Valid @RequestBody PatientRequestDTO dto) {

        return service.update(id, dto);
    }

    /* =====================================================
       READ ONE
       ===================================================== */

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public PatientResponseDTO getById(@PathVariable UUID id) {

        return service.getById(id);
    }

    /* =====================================================
       SEARCH + PAGINATION
       ===================================================== */

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN','INFIRMIER')")
    public Page<PatientResponseDTO> search(

            @RequestParam(required = false) String nom,
            @RequestParam(required = false) String prenom,
            @RequestParam(required = false) Sexe sexe,
            @RequestParam(required = false) GroupeSanguin groupeSanguin,
            @RequestParam(required = false) String allergie,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate dateNaissance,

            Pageable pageable
    ) {

        return service.search(
                nom,
                prenom,
                sexe,
                groupeSanguin,
                allergie,
                dateNaissance,
                pageable
        );
    }

    /* =====================================================
       STATISTIQUES
       ===================================================== */

    @GetMapping("/stats/sexe/{sexe}")
    @PreAuthorize("hasRole('ADMIN')")
    public long countBySexe(@PathVariable Sexe sexe) {
        return service.countBySexe(sexe);
    }

    @GetMapping("/stats/groupe/{groupe}")
    @PreAuthorize("hasRole('ADMIN')")
    public long countByGroupe(@PathVariable GroupeSanguin groupe) {
        return service.countByGroupeSanguin(groupe);
    }

    @GetMapping("/recent")
    @PreAuthorize("hasAnyRole('ADMIN','MEDECIN')")
    public java.util.List<PatientResponseDTO> last10() {
        return service.getLast10Patients();
    }

    /* =====================================================
       DELETE
       ===================================================== */

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable UUID id) {

        service.delete(id);
    }
}