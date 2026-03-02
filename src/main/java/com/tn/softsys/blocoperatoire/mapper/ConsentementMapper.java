package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ConsentementMapper {

    public ConsentementResponseDTO toDTO(Consentement entity) {

        return ConsentementResponseDTO.builder()
                .consentId(entity.getConsentId())
                .type(entity.getType())
                .date(entity.getDate())
                .valide(entity.getValide())
                .patientId(entity.getPatient().getPatientId())
                .interventionId(entity.getIntervention().getInterventionId())
                .build();
    }
}