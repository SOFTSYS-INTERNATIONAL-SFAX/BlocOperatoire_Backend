package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.Score;
import com.tn.softsys.blocoperatoire.dto.scores.ScoreResponseDTO;

public class ScoreMapper {

    private ScoreMapper() {}

    public static ScoreResponseDTO toResponse(Score score) {

        if (score == null) {
            return null;
        }

        return ScoreResponseDTO.builder()
                .scoreId(score.getScoreId())
                .scoreType(
                        score.getScoreType() != null
                                ? score.getScoreType().name()
                                : null
                )
                .valeur(score.getValeur())
                .algorithmVersion(score.getAlgorithmVersion())
                .dateCalcul(score.getDateCalcul())

                // ✅ AJOUT CRITIQUE
                .interventionId(
                        score.getIntervention() != null
                                ? score.getIntervention().getInterventionId()
                                : null
                )

                .build();
    }
}