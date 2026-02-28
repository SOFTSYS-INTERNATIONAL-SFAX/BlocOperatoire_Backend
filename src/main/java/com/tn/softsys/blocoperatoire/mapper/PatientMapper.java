package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    public Patient toEntity(PatientRequestDTO dto) {
        return Patient.builder()
                .identiteFHIR(dto.getIdentiteFHIR())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .nationalite(dto.getNationalite())
                .build();
    }

    public void updateEntity(Patient patient, PatientRequestDTO dto) {
        patient.setNom(dto.getNom());
        patient.setPrenom(dto.getPrenom());
        patient.setDateNaissance(dto.getDateNaissance());
        patient.setSexe(dto.getSexe());
        patient.setNationalite(dto.getNationalite());
    }

    public PatientResponseDTO toResponse(Patient patient) {
        return PatientResponseDTO.builder()
                .patientId(patient.getPatientId())
                .identiteFHIR(patient.getIdentiteFHIR())
                .nom(patient.getNom())
                .prenom(patient.getPrenom())
                .dateNaissance(patient.getDateNaissance())
                .sexe(patient.getSexe())
                .nationalite(patient.getNationalite())
                .createdAt(patient.getCreatedAt())
                .updatedAt(patient.getUpdatedAt())
                .build();
    }
}