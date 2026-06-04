package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.glasgow.Glasgow;
import com.tn.softsys.blocoperatoire.dto.scores.gcs.GcsResponseDTO;

public class GcsMapper {

    public static GcsResponseDTO toResponse(Glasgow entity) {

        return GcsResponseDTO.builder()
                .scoreId(entity.getScoreId())
                .total(entity.getValeur())
                .gravite(
                        entity.getGravite() != null
                                ? entity.getGravite().name()
                                : null
                )
                .algorithmVersion(entity.getAlgorithmVersion())
                .dateCalcul(entity.getDateCalcul())
                .build();
    }
}