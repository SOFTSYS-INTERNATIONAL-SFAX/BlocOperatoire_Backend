package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.Reanimation;
import com.tn.softsys.blocoperatoire.dto.reanimation.ReanimationResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ReanimationMapper {

    public ReanimationResponseDTO toDTO(Reanimation entity) {

        return ReanimationResponseDTO.builder()
                .reaId(entity.getReaId())
                .sspiId(entity.getSspi().getSspiId())
                .dateEntree(entity.getDateEntree())
                .dateSortie(entity.getDateSortie())
                .build();
    }
}