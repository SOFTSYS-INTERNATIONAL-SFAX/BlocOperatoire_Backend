package com.tn.softsys.blocoperatoire.dto.scores.sofa;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SofaResponseDTO {

    private UUID scoreId;

    private int respiratoryScore;
    private int coagulationScore;
    private int liverScore;
    private int cardiovascularScore;
    private int neurologicalScore;
    private int renalScore;

    private int totalScore;

    private boolean sepsisAlert;

    private String algorithmVersion;

    private LocalDateTime dateCalcul;
}