package com.tn.softsys.blocoperatoire.mapper.scores;

import com.tn.softsys.blocoperatoire.domain.scores.eva.Eva;
import com.tn.softsys.blocoperatoire.dto.scores.eva.EvaResponseDTO;

public class EvaMapper {

    public static EvaResponseDTO toResponse(Eva entity, boolean severeAlert) {
        return EvaResponseDTO.builder()
                .scoreId(entity.getScoreId())
                .value(entity.getValue())
                .alertLevel(entity.getAlertLevel().name())
                .severeAlert(severeAlert)
                .algorithmVersion(entity.getAlgorithmVersion())
                .dateCalcul(entity.getDateCalcul())
                .build();
    }
}