package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Consentement;
import com.tn.softsys.blocoperatoire.domain.ConsentementStatut;
import com.tn.softsys.blocoperatoire.dto.consentement.ConsentementResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ConsentementMapper {

    public ConsentementResponseDTO toDTO(Consentement entity) {

        return ConsentementResponseDTO.builder()
                .consentId(entity.getConsentId())
                .type(entity.getType())
                .date(entity.getDate())
                .valide(resolveValide(entity))
                .statut(resolveStatut(entity))
                .patientId(entity.getPatient().getPatientId())
                .interventionId(entity.getIntervention().getInterventionId())
                .verifiedAt(entity.getVerifiedAt())
                .verifiedByUserId(entity.getVerifiedBy() != null ? entity.getVerifiedBy().getUserId() : null)
                .verifiedByName(entity.getVerifiedByName())
                .build();
    }

    private ConsentementStatut resolveStatut(Consentement entity) {
        if (entity.getStatut() != null) {
            return entity.getStatut();
        }

        return Boolean.TRUE.equals(entity.getValide())
                ? ConsentementStatut.VERIFIE
                : ConsentementStatut.BROUILLON;
    }

    private boolean resolveValide(Consentement entity) {
        return ConsentementStatut.VERIFIE.equals(resolveStatut(entity))
                || Boolean.TRUE.equals(entity.getValide());
    }
}
