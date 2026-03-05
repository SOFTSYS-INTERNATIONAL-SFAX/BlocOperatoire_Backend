package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.apache.ApacheII;
import com.tn.softsys.blocoperatoire.dto.scores.apache.ApacheResponseDTO;

public class ApacheMapper {

    public static ApacheResponseDTO toResponse(ApacheII entity) {
        return ApacheResponseDTO.builder()
                .scoreId(entity.getScoreId())
                .totalScore(entity.getValeur())
                .mortalityProbability(entity.getMortalityProbability())
                .algorithmVersion(entity.getAlgorithmVersion())
                .dateCalcul(entity.getDateCalcul())
                .build();
    }
}