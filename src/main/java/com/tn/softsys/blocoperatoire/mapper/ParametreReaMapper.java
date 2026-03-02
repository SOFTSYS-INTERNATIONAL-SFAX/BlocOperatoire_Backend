package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.ParametreRea;
import com.tn.softsys.blocoperatoire.dto.parametrerea.ParametreReaResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ParametreReaMapper {

    public ParametreReaResponseDTO toDTO(ParametreRea entity) {

        return ParametreReaResponseDTO.builder()
                .paramId(entity.getParamId())
                .reaId(entity.getReanimation().getReaId())
                .dateMesure(entity.getDateMesure())
                .ventilation(entity.getVentilation())
                .vasopresseurActif(entity.getVasopresseurActif())
                .diurese(entity.getDiurese())
                .build();
    }
}