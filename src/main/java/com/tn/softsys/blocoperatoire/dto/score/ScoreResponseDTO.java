package com.tn.softsys.blocoperatoire.dto.score;

import com.tn.softsys.blocoperatoire.domain.ScoreEtat;
import com.tn.softsys.blocoperatoire.domain.ScoreType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class ScoreResponseDTO {

    private UUID scoreId;
    private ScoreType type;
    private Integer valeur;
    private ScoreEtat etat;
    private LocalDateTime dateCalcul;
    private UUID interventionId;
}