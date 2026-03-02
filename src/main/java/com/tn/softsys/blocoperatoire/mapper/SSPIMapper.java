package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.SSPI;
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
                .build();
    }
}