package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.MouvementCorps;
import com.tn.softsys.blocoperatoire.dto.mouvement.MouvementCorpsResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class MouvementCorpsMapper {

    public MouvementCorpsResponseDTO toDTO(MouvementCorps entity) {

        return MouvementCorpsResponseDTO.builder()
                .mouvementId(entity.getMouvementId())
                .caseId(entity.getCaseMortuaire().getCaseId())
                .dateMouvement(entity.getDateMouvement())
                .typeMouvement(entity.getTypeMouvement())
                .build();
    }
}