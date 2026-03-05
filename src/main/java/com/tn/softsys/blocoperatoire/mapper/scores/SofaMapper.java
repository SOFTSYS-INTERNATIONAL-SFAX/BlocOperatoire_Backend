package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.sofa.Sofa;
import com.tn.softsys.blocoperatoire.dto.scores.sofa.SofaResponseDTO;




public class SofaMapper {

    private SofaMapper() {}

    public static SofaResponseDTO toResponse(Sofa sofa) {

        if (sofa == null) {
            return null;
        }

        return SofaResponseDTO.builder()
                .scoreId(sofa.getScoreId())
                .respiratoryScore(sofa.getRespiratoryScore())
                .coagulationScore(sofa.getCoagulationScore())
                .liverScore(sofa.getLiverScore())
                .cardiovascularScore(sofa.getCardiovascularScore())
                .neurologicalScore(sofa.getNeurologicalScore())
                .renalScore(sofa.getRenalScore())
                .totalScore(sofa.getValeur())
                .sepsisAlert(sofa.isSepsisAlert())   // 🔥 direct depuis entité
                .algorithmVersion(sofa.getAlgorithmVersion())
                .dateCalcul(sofa.getDateCalcul())
                .build();
    }
}