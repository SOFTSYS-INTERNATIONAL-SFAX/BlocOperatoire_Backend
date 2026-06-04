package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.SSPI;
import com.tn.softsys.blocoperatoire.domain.User;
import com.tn.softsys.blocoperatoire.dto.sspi.SSPIResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SSPIMapper {

    public SSPIResponseDTO toDTO(SSPI entity) {
        return SSPIResponseDTO.builder()
                .sspiId(entity.getSspiId())
                .interventionId(entity.getIntervention().getInterventionId())
                .heureEntree(entity.getHeureEntree())
                .heureSortie(entity.getHeureSortie())
                .posteCode(entity.getPosteCode())
                .destinationSortie(entity.getDestinationSortie())
                .motifSortie(entity.getMotifSortie())
                .aldreteSortie(entity.getAldreteSortie())
                .decisionMedicale(entity.getDecisionMedicale())
                .observationsSortie(entity.getObservationsSortie())
                .transmissionResume(entity.getTransmissionResume())
                .sortieValideeParUserId(entity.getSortieValideePar() != null ? entity.getSortieValideePar().getUserId() : null)
                .sortieValideeParName(buildUserLabel(entity.getSortieValideePar()))
                .surveillanceCount(entity.getSurveillances() != null ? entity.getSurveillances().size() : 0)
                .incidentCount(entity.getIncidents() != null ? entity.getIncidents().size() : 0)
                .build();
    }

    private String buildUserLabel(User user) {
        if (user == null) {
            return null;
        }

        String fullName = ((user.getPrenom() != null ? user.getPrenom() : "") + " " +
                (user.getNom() != null ? user.getNom() : "")).trim();

        return fullName.isBlank() ? user.getEmail() : "Dr. " + fullName;
    }
}