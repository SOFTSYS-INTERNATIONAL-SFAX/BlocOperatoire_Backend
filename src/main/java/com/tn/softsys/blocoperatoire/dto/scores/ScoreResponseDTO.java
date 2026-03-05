package com.tn.softsys.blocoperatoire.dto.scores;

import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScoreResponseDTO {

    private UUID scoreId;
    private String scoreType;
    private Integer valeur;
    private String algorithmVersion;
    private LocalDateTime dateCalcul;

    private UUID interventionId;   // 🔥 doit exister
}