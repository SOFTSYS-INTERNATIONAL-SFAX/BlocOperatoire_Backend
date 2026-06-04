package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.SurveillanceSSPI;
import com.tn.softsys.blocoperatoire.dto.surveillance.SurveillanceResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class SurveillanceMapper {

    public SurveillanceResponseDTO toDTO(SurveillanceSSPI entity) {
        return SurveillanceResponseDTO.builder()
                .surveillanceId(entity.getSurveillanceId())
                .tensionArterielle(entity.getTensionArterielle())
                .frequenceCardiaque(entity.getFrequenceCardiaque())
                .spo2(entity.getSpo2())
                .temperature(entity.getTemperature())
                .build();
    }
}