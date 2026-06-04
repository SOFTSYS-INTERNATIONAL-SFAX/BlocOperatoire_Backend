package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.asa.Asa;
import com.tn.softsys.blocoperatoire.dto.scores.asa.AsaResponseDTO;

public class AsaMapper {

    public static AsaResponseDTO toResponse(Asa entity) {
        return AsaResponseDTO.builder()
                .scoreId(entity.getScoreId())
                .asaClass(entity.getAsaClass().name())
                .emergency(entity.isEmergency())
                .finalClassification(entity.getFinalClassification())
                .numericValue(entity.getValeur())
                .algorithmVersion(entity.getAlgorithmVersion())
                .dateCalcul(entity.getDateCalcul())
                .build();
    }
}