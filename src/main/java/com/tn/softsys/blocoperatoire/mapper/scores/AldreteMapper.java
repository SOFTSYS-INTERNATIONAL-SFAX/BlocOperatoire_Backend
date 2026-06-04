package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.aldrete.Aldrete;
import com.tn.softsys.blocoperatoire.dto.scores.aldrete.AldreteResponseDTO;

public class AldreteMapper {

    public static AldreteResponseDTO toResponse(Aldrete entity) {
        boolean eligible = entity.getValeur() >= 9;

        return AldreteResponseDTO.builder()
                .scoreId(entity.getScoreId())
                .total(entity.getValeur())
                .decision(entity.getDecision().name())
                .eligibleForDischarge(eligible)
                .algorithmVersion(entity.getAlgorithmVersion())
                .dateCalcul(entity.getDateCalcul())
                .build();
    }
}