package com.tn.softsys.blocoperatoire.dto.scores.apache;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApacheResponseDTO {

    private UUID scoreId;

    private int totalScore;

    private double mortalityProbability;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}