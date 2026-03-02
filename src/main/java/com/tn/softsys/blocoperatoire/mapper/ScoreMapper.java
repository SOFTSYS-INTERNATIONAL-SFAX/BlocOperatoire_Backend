package com.tn.softsys.blocoperatoire.mapper;

import com.tn.softsys.blocoperatoire.domain.*;
import com.tn.softsys.blocoperatoire.dto.score.ScoreResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class ScoreMapper {

    public ScoreResponseDTO toDTO(Score score) {

        return ScoreResponseDTO.builder()
                .scoreId(score.getScoreId())
                .type(resolveType(score))
                .valeur(score.getValeur())
                .etat(score.getEtat())
                .dateCalcul(score.getDateCalcul())
                .interventionId(score.getIntervention().getInterventionId())
                .build();
    }

    public ScoreType resolveType(Score score) {

        if (score instanceof ASA) return ScoreType.ASA;
        if (score instanceof Aldrete) return ScoreType.ALDRETE;
        if (score instanceof SOFA) return ScoreType.SOFA;
        if (score instanceof APACHEII) return ScoreType.APACHEII;
        if (score instanceof Glasgow) return ScoreType.GLASGOW;
        if (score instanceof EVA) return ScoreType.EVA;

        throw new IllegalStateException("Unknown score type");
    }
}