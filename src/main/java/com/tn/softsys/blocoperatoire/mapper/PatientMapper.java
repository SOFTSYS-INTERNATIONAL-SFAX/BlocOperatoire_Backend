package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Patient;
import com.tn.softsys.blocoperatoire.dto.patient.PatientRequestDTO;
import com.tn.softsys.blocoperatoire.dto.patient.PatientResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class PatientMapper {

    /* ================= TO ENTITY ================= */

    public Patient toEntity(PatientRequestDTO dto) {

        return Patient.builder()
                .identiteFHIR(dto.getIdentiteFHIR())
                .mrn(dto.getMrn())
                .nom(dto.getNom())
                .prenom(dto.getPrenom())
                .dateNaissance(dto.getDateNaissance())
                .sexe(dto.getSexe())
                .nationalite(dto.getNationalite())
                .groupeSanguin(dto.getGroupeSanguin())
                .allergies(dto.getAllergies())
                .traitementsEnCours(dto.getTraitementsEnCours())
                .antecedentsMedicaux(dto.getAntecedentsMedicaux())
                .tailleCm(dto.getTailleCm())
                .poidsKg(dto.getPoidsKg())
                .contactUrgenceNom(dto.getContactUrgenceNom())
                .contactUrgenceTelephone(dto.getContactUrgenceTelephone())
                .build();
    }

    /* ================= UPDATE ENTITY ================= */

    public void updateEntity(Patient entity, PatientRequestDTO dto) {

        entity.setNom(dto.getNom());
        entity.setPrenom(dto.getPrenom());
        entity.setDateNaissance(dto.getDateNaissance());
        entity.setSexe(dto.getSexe());
        entity.setNationalite(dto.getNationalite());
        entity.setGroupeSanguin(dto.getGroupeSanguin());
        entity.setAllergies(dto.getAllergies());
        entity.setTraitementsEnCours(dto.getTraitementsEnCours());
        entity.setAntecedentsMedicaux(dto.getAntecedentsMedicaux());
        entity.setTailleCm(dto.getTailleCm());
        entity.setPoidsKg(dto.getPoidsKg());
        entity.setContactUrgenceNom(dto.getContactUrgenceNom());
        entity.setContactUrgenceTelephone(dto.getContactUrgenceTelephone());
    }

    /* ================= TO RESPONSE ================= */

    public PatientResponseDTO toResponse(Patient entity) {

        return PatientResponseDTO.builder()
                .patientId(entity.getPatientId())
                .identiteFHIR(entity.getIdentiteFHIR())
                .mrn(entity.getMrn())
                .nom(entity.getNom())
                .prenom(entity.getPrenom())
                .dateNaissance(entity.getDateNaissance())
                .sexe(entity.getSexe())
                .nationalite(entity.getNationalite())
                .groupeSanguin(entity.getGroupeSanguin())
                .allergies(entity.getAllergies())
                .traitementsEnCours(entity.getTraitementsEnCours())
                .antecedentsMedicaux(entity.getAntecedentsMedicaux())
                .tailleCm(entity.getTailleCm())
                .poidsKg(entity.getPoidsKg())
                .imc(calculateImc(entity.getTailleCm(), entity.getPoidsKg()))
                .contactUrgenceNom(entity.getContactUrgenceNom())
                .contactUrgenceTelephone(entity.getContactUrgenceTelephone())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    /* ================= CALCUL IMC ================= */

    private Double calculateImc(Double tailleCm, Double poidsKg) {

        if (tailleCm == null || poidsKg == null) return null;

        double tailleMetre = tailleCm / 100.0;
        return poidsKg / (tailleMetre * tailleMetre);
    }
}