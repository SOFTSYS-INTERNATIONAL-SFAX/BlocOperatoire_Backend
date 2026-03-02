package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.CaseMortuaire;
import com.tn.softsys.blocoperatoire.dto.casemor.CaseMortuaireResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CaseMortuaireMapper {

    public CaseMortuaireResponseDTO toDTO(CaseMortuaire entity) {

        return CaseMortuaireResponseDTO.builder()
                .caseId(entity.getCaseId())
                .numeroCase(entity.getNumeroCase())
                .occupee(entity.getOccupee())
                .morgueId(entity.getMorgue().getMorgueId())
                .decesId(entity.getDeces() != null
                        ? entity.getDeces().getDecesId()
                        : null)
                .build();
    }
}