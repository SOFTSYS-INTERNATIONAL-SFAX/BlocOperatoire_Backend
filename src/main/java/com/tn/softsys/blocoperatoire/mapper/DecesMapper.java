package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Deces;
import com.tn.softsys.blocoperatoire.dto.deces.DecesResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class DecesMapper {

    public DecesResponseDTO toDTO(Deces entity) {

        return DecesResponseDTO.builder()
                .decesId(entity.getDecesId())
                .interventionId(entity.getIntervention().getInterventionId())
                .dateDeces(entity.getDateDeces())
                .cause(entity.getCause())
                .constatPar(entity.getConstatPar())
                .build();
    }
}